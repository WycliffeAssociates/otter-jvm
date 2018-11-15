package org.wycliffeassociates.otter.jvm.persistence.repositories

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.jooq.DSLContext
import org.wycliffeassociates.otter.common.collections.tree.Tree
import org.wycliffeassociates.otter.common.collections.tree.TreeNode
import org.wycliffeassociates.otter.common.data.model.Content
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.common.data.model.ResourceMetadata
import org.wycliffeassociates.otter.common.domain.mapper.mapToMetadata
import org.wycliffeassociates.otter.common.persistence.IDirectoryProvider
import org.wycliffeassociates.otter.common.persistence.repositories.ICollectionRepository
import org.wycliffeassociates.otter.jvm.persistence.resourcecontainer.IResourceContainerIO
import org.wycliffeassociates.otter.jvm.persistence.resourcecontainer.ResourceContainerIO
import org.wycliffeassociates.otter.jvm.persistence.database.AppDatabase
import org.wycliffeassociates.otter.jvm.persistence.database.queries.DeriveProjectQuery
import org.wycliffeassociates.otter.jvm.persistence.entities.CollectionEntity
import org.wycliffeassociates.otter.jvm.persistence.entities.ResourceMetadataEntity
import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.ChunkMapper
import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.CollectionMapper
import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.LanguageMapper
import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.ResourceMetadataMapper
import org.wycliffeassociates.resourcecontainer.ResourceContainer
import org.wycliffeassociates.resourcecontainer.entity.*
import java.io.File

class CollectionRepository(
        private val database: AppDatabase,
        private val directoryProvider: IDirectoryProvider,
        private val collectionMapper: CollectionMapper = CollectionMapper(),
        private val contentMapper: ContentMapper = ContentMapper(),
        private val metadataMapper: ResourceMetadataMapper = ResourceMetadataMapper(),
        private val languageMapper: LanguageMapper = LanguageMapper(),
        private val deriveProjectQuery: DeriveProjectQuery = DeriveProjectQuery(),
        private val rcIO: IResourceContainerIO = ResourceContainerIO(directoryProvider)
) : ICollectionRepository {
    private val collectionDao = database.getCollectionDao()
    private val contentDao = database.getContentDao()
    private val metadataDao = database.getResourceMetadataDao()
    private val languageDao = database.getLanguageDao()

    override fun delete(obj: Collection): Completable {
        return Completable
                .fromAction {
                    collectionDao.delete(collectionMapper.mapToEntity(obj))
                }
                .subscribeOn(Schedulers.io())
    }

    override fun deleteProject(project: Collection, deleteAudio: Boolean): Completable {
        return Completable
                .fromAction {
                    // 1. Delete the project collection from the database. The associated chunks, takes, and links
                    //    should be cascade deleted
                    collectionDao.delete(collectionMapper.mapToEntity(project))
                    // 2. Load the resource container
                    val metadata = project.resourceContainer
                    if (metadata != null) {
                        val container = ResourceContainer.load(metadata.path)
                        // 3. Remove the project from the manifest
                        container.manifest.projects = container.manifest.projects.filter { it.identifier != project.slug }
                        // 4a. If the manifest has more projects, write out the new manifest
                        if (container.manifest.projects.isNotEmpty()) {
                            container.writeManifest()
                        } else {
                            // 4b. If the manifest has no projects left, delete the RC folder and the metadata from the database
                            metadata.path.deleteRecursively()
                            metadataDao.delete(metadataMapper.mapToEntity(metadata))
                        }
                    }
                    // 5. If project audio should be deleted, get the folder for the project audio and delete it
                    if (deleteAudio) {
                        val audioDirectory = directoryProvider.getProjectAudioDirectory(project, ".").parentFile
                        audioDirectory.deleteRecursively()
                    }
                }.subscribeOn(Schedulers.io())
    }

    override fun getAll(): Single<List<Collection>> {
        return Single
                .fromCallable {
                    collectionDao
                            .fetchAll()
                            .map(this::buildCollection)
                }
                .subscribeOn(Schedulers.io())
    }

    override fun getRootProjects(): Single<List<Collection>> {
        return Single
                .fromCallable {
                    collectionDao
                            .fetchAll()
                            .filter { it.sourceFk != null && it.label == "project" }
                            .map(this::buildCollection)
                }
                .subscribeOn(Schedulers.io())
    }

    override fun getRootSources(): Single<List<Collection>> {
        return Single
                .fromCallable {
                    collectionDao
                            .fetchAll()
                            .filter { it.parentFk == null && it.sourceFk == null }
                            .map(this::buildCollection)
                }
                .subscribeOn(Schedulers.io())
    }

    override fun getSource(project: Collection): Maybe<Collection> {
        return Maybe
                .fromCallable {
                    buildCollection(
                            collectionDao.fetchSource(collectionDao.fetchById(project.id))
                    )
                }
                .onErrorComplete()
                .subscribeOn(Schedulers.io())
    }

    override fun getBySlugAndContainer(slug: String, container: ResourceMetadata): Maybe<Collection> {
        return Maybe
                .fromCallable {
                    buildCollection(collectionDao.fetchBySlugAndContainerId(slug, container.id))
                }
                .onErrorComplete()
                .subscribeOn(Schedulers.io())
    }

    override fun getChildren(collection: Collection): Single<List<Collection>> {
        return Single
                .fromCallable {
                    collectionDao
                            .fetchChildren(collectionMapper.mapToEntity(collection))
                            .map(this::buildCollection)
                }
                .subscribeOn(Schedulers.io())
    }

    override fun updateSource(collection: Collection, newSource: Collection): Completable {
        return Completable
                .fromAction {
                    val entity = collectionDao.fetchById(collection.id)
                    entity.sourceFk = newSource.id
                    collectionDao.update(entity)
                }
                .subscribeOn(Schedulers.io())
    }

    override fun updateParent(collection: Collection, newParent: Collection): Completable {
        return Completable
                .fromAction {
                    val entity = collectionDao.fetchById(collection.id)
                    entity.parentFk = newParent.id
                    collectionDao.update(entity)
                }
                .subscribeOn(Schedulers.io())
    }

    override fun insert(collection: Collection): Single<Int> {
        return Single
                .fromCallable {
                    collectionDao.insert(collectionMapper.mapToEntity(collection))
                }
                .subscribeOn(Schedulers.io())
    }

    override fun update(obj: Collection): Completable {
        return Completable
                .fromAction {
                    val entity = collectionDao.fetchById(obj.id)
                    val newEntity = collectionMapper.mapToEntity(obj, entity.parentFk, entity.sourceFk)
                    collectionDao.update(newEntity)
                }
                .subscribeOn(Schedulers.io())
    }

    override fun deriveProject(source: Collection, language: Language): Completable {
        return Completable
                .fromAction {
                    database.transaction { dsl ->
                        val metadataEntity = createMetadataForNewProject(source, language, dsl)

                        // Insert the derived project
                        val sourceEntity = collectionDao.fetchById(source.id, dsl)
                        val projectEntity = sourceEntity
                                // parentFk null for now. May be non-null if derivative categories added
                                .copy(id = 0, metadataFk = metadataEntity.id, parentFk = null, sourceFk = sourceEntity.id)
                        projectEntity.id = collectionDao.insert(projectEntity, dsl)

                        // Execute the derive project database query
                        deriveProjectQuery.execute(sourceEntity.id, projectEntity.id, metadataEntity.id, dsl)

                        // Add the project to the resource container if necessary
                        addProjectToContainer(sourceEntity, projectEntity, metadataEntity)
                    }
                }
                .subscribeOn(Schedulers.io())
    }

    private fun createMetadataForNewProject(
            source: Collection,
            language: Language,
            dsl: DSLContext
    ): ResourceMetadataEntity {
        // Check for existing resource containers
        val existingMetadata = metadataDao.fetchAll(dsl)
        val matches = existingMetadata.filter {
            it.identifier == source.resourceContainer?.identifier
                    && it.languageFk == language.id
        }
        return if (matches.isEmpty()) {
            // This combination of identifier and language does not already exist; create it
            val container = rcIO.createForNewProject(source, language)
            // Convert DublinCore to ResourceMetadata
            val metadata = container.manifest.dublinCore
                    .mapToMetadata(container.dir, language)

            // Insert ResourceMetadata into database
            val entity = metadataMapper.mapToEntity(metadata)
            entity.id = metadataDao.insert(entity, dsl)
            /* return@if */ entity
        } else {
            // Use the existing metadata
            /* return@if */ matches.first()
        }
    }

    private fun addProjectToContainer(
            source: CollectionEntity,
            projectEntity: CollectionEntity,
            metadataEntity: ResourceMetadataEntity
    ) {
        // Add a project to the container if necessary
        // Load the existing resource container and see if we need to add another project
        val container = rcIO.load(File(metadataEntity.path))
        if (container.manifest.projects.none { it.identifier == source.slug }) {
            container.manifest.projects = container.manifest.projects.plus(
                    project {
                        sort = if (metadataEntity.subject.toLowerCase() == "bible"
                                && projectEntity.sort > 39) {
                            projectEntity.sort + 1
                        } else {
                            projectEntity.sort
                        }
                        identifier = projectEntity.slug
                        path = "./${projectEntity.slug}"
                        // This title will not be localized into the target language
                        title = projectEntity.title
                        // Unable to get these fields from the source collection
                        categories = listOf()
                        versification = ""
                    }
            )
            // Update the container
            container.write()
        }
    }

    private fun buildCollection(entity: CollectionEntity): Collection {
        var metadata: ResourceMetadata? = null
        entity.metadataFk?.let {
            val metadataEntity = metadataDao.fetchById(it)
            val language = languageMapper.mapFromEntity(languageDao.fetchById(metadataEntity.languageFk))
            metadata = metadataMapper.mapFromEntity(metadataEntity, language)
        }

        return collectionMapper.mapFromEntity(entity, metadata)
    }
  
    override fun importResourceContainer(rc: ResourceContainer, rcTree: Tree, languageSlug: String): Completable {
        return Completable.fromAction {
            database.transaction { dsl ->
                val language = languageMapper.mapFromEntity(languageDao.fetchBySlug(languageSlug, dsl))
                val metadata = rc.manifest.dublinCore.mapToMetadata(rc.dir, language)
                val metadataId = metadataDao.insert(metadataMapper.mapToEntity(metadata), dsl)

                importCollection(null, metadataId, rcTree, dsl)
            }
        }.subscribeOn(Schedulers.io())
    }

    private fun importNode(parentId: Int, metadataId: Int, node: TreeNode, dsl: DSLContext) {
        when(node) {
            is Tree -> {
                importCollection(parentId, metadataId, node, dsl)
            }
            is TreeNode -> {
                importContent(parentId, node, dsl)
            }
        }
    }

    private fun importCollection(parentId: Int?, metadataId: Int, node: Tree, dsl: DSLContext){
        val collection = node.value
        if (collection is Collection) {
            val entity = collectionMapper.mapToEntity(collection)
            entity.parentFk = parentId
            entity.metadataFk = metadataId
            val id = collectionDao.insert(entity, dsl)
            for (node in node.children) {
                importNode(id, metadataId, node, dsl)
            }
        }
    }

    private fun importContent(parentId: Int, node: TreeNode, dsl: DSLContext) {
        val content = node.value
        if (content is Content) {
            val entity = contentMapper.mapToEntity(content)
            entity.collectionFk = parentId
            contentDao.insert(entity, dsl)
        }
    }
}