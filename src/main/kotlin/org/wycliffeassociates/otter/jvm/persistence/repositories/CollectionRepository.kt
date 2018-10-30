package org.wycliffeassociates.otter.jvm.persistence.repositories

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.jooq.DSLContext
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.common.data.model.ResourceMetadata
import org.wycliffeassociates.otter.common.domain.mapToMetadata
import org.wycliffeassociates.otter.common.persistence.IDirectoryProvider
import org.wycliffeassociates.otter.common.persistence.repositories.ICollectionRepository
import org.wycliffeassociates.otter.jvm.persistence.database.AppDatabase
import org.wycliffeassociates.otter.jvm.persistence.entities.CollectionEntity
import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.CollectionMapper
import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.LanguageMapper
import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.ResourceMetadataMapper
import org.wycliffeassociates.resourcecontainer.ResourceContainer
import org.wycliffeassociates.resourcecontainer.entity.*
import java.lang.NullPointerException
import java.time.LocalDate


class CollectionRepository(
        private val database: AppDatabase,
        private val collectionMapper: CollectionMapper = CollectionMapper(),
        private val metadataMapper: ResourceMetadataMapper = ResourceMetadataMapper(),
        private val languageMapper: LanguageMapper = LanguageMapper(),
        private val directoryProvider: IDirectoryProvider
) : ICollectionRepository {

    private val collectionDao = database.getCollectionDao()
    private val metadataDao = database.getResourceMetadataDao()
    private val languageDao = database.getLanguageDao()
    private val chunkDao = database.getChunkDao()

    override fun delete(obj: Collection): Completable {
        return Completable
                .fromAction {
                    collectionDao.delete(collectionMapper.mapToEntity(obj))
                }
                .subscribeOn(Schedulers.io())
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

    override fun insert(obj: Collection): Single<Int> {
        return Single
                .fromCallable {
                    collectionDao.insert(collectionMapper.mapToEntity(obj))
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

    private fun createResourceContainer(source: Collection, targetLanguage: Language): ResourceContainer {
        val metadata = source.resourceContainer
        metadata ?: throw NullPointerException("Source has no resource metadata")

        val slug = "${targetLanguage.slug}_${metadata.identifier}"
        val directory = directoryProvider.resourceContainerDirectory.resolve(slug)
        val container = ResourceContainer.create(directory) {
            // Set up the manifest
            manifest = manifest {
                dublinCore = dublincore {
                    identifier = metadata.identifier
                    issued = LocalDate.now().toString()
                    modified = LocalDate.now().toString()
                    language = org.wycliffeassociates.resourcecontainer.entity.language {
                        identifier = targetLanguage.slug
                        direction = targetLanguage.direction
                        title = targetLanguage.name
                    }
                    format = "text/usfm"
                    subject = metadata.subject
                    type = "book"
                    title = metadata.title
                }
            }
        }
        container.write()
        return container
    }

    private fun copyCollectionEntityHierarchy(parentId: Int?, root: CollectionEntity, metadataId: Int, dsl: DSLContext) {
        // Copy the root collection entity
        val derived = root.copy(id = 0, metadataFk = metadataId, parentFk = parentId, sourceFk = root.id)
        derived.id = collectionDao.insert(derived, dsl)

        // Copy content associated with this collection
        val chunks = chunkDao.fetchByCollectionId(root.id, dsl)
        for (chunk in chunks) {
            val derivedChunk = chunk.copy(id = 0, collectionFk = derived.id, selectedTakeFk = null)
            derivedChunk.id = chunkDao.insert(derivedChunk, dsl)
            chunkDao.updateSources(derivedChunk, listOf(chunk), dsl)
        }

        // Get all the subcollections
        val subcollections = collectionDao.fetchChildren(root, dsl)
        // Duplicate them collection
        for (subcollection in subcollections) {
            copyCollectionEntityHierarchy(derived.id, subcollection, metadataId, dsl)
        }

    }

    override fun deriveProject(source: Collection, language: Language): Completable {
        return Completable
                .fromAction {
                    database.transaction { dsl ->
                        val container = createResourceContainer(source, language)
                        // Convert DublinCore to ResourceMetadata
                        val metadata = container.manifest.dublinCore
                                .mapToMetadata(container.dir, language)

                        // Insert ResourceMetadata into database
                        val metadataEntity = metadataMapper.mapToEntity(metadata)
                        metadataEntity.id = metadataDao.insert(metadataEntity, dsl)

                        // Traverse and duplicate the source tree (down to the chunk level)
                        val rootEntity = collectionDao.fetchById(source.id, dsl)
                        // TODO: What about parent RC/categories?
                        copyCollectionEntityHierarchy(null, rootEntity, metadataEntity.id, dsl)
                    }
                }
                .subscribeOn(Schedulers.io())
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
}