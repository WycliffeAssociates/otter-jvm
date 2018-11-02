package org.wycliffeassociates.otter.jvm.persistence.repositories

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.jooq.DSLContext
import org.wycliffeassociates.otter.common.collections.tree.Tree
import org.wycliffeassociates.otter.common.collections.tree.TreeNode
import org.wycliffeassociates.otter.common.data.model.Chunk
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.ResourceMetadata
import org.wycliffeassociates.otter.common.persistence.repositories.ICollectionRepository
import org.wycliffeassociates.otter.jvm.persistence.database.AppDatabase
import org.wycliffeassociates.otter.jvm.persistence.entities.CollectionEntity
import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.CollectionMapper
import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.LanguageMapper
import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.ResourceMetadataMapper
import org.wycliffeassociates.resourcecontainer.ResourceContainer
import org.wycliffeassociates.otter.common.domain.mapper.mapToMetadata
import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.ChunkMapper


class CollectionRepository(
        private val database: AppDatabase,
        private val collectionMapper: CollectionMapper = CollectionMapper(),
        private val chunkMapper: ChunkMapper = ChunkMapper(),
        private val metadataMapper: ResourceMetadataMapper = ResourceMetadataMapper(),
        private val languageMapper: LanguageMapper = LanguageMapper()
) : ICollectionRepository {
    private val collectionDao = database.getCollectionDao()
    private val chunkDao = database.getChunkDao()
    private val metadataDao = database.getResourceMetadataDao()
    private val languageDao = database.getLanguageDao()

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
                    val newEntity = collectionMapper.mapToEntity(obj)
                    newEntity.parentFk = entity.parentFk
                    newEntity.sourceFk = entity.sourceFk
                    collectionDao.update(newEntity)
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

    override fun importResourceContainer(rcTree: Tree, languageSlug: String): Completable {
        TODO()
//        database.transaction { dsl ->
//            val language = languageMapper.mapFromEntity(languageDao.fetchBySlug(languageSlug, dsl))
//            val metadata = rc.manifest.dublinCore.mapToMetadata(rc.dir, language)
//            val metadataId = metadataDao.insert(metadataMapper.mapToEntity(metadata))
//
//            val root = rcTree.value as Collection
//            val rootId = collectionDao.insert(collectionMapper.mapToEntity(root))
//            for (node in rcTree.children) {
//                importNode(rootId, metadataId, node)
//            }
//        }
    }

    private fun importNode(parentId: Int, metadataId: Int, node: TreeNode) {
        when(node) {
            is Tree -> {
                importCollection(parentId, metadataId, node)
            }
            is TreeNode -> {
                importChunk(parentId, node)
            }
        }
    }

    private fun importCollection(parentId: Int, metadataId: Int, node: Tree){
        val collection = node.value as Collection
        val entity = collectionMapper.mapToEntity(collection)
        val id = collectionDao.insert(entity)
        entity.parentFk = parentId
        entity.metadataFk = metadataId
        collectionDao.update(entity)
        for (node in node.children) {
            importNode(id, metadataId, node)
        }
    }

    private fun importChunk(parentId: Int, node: TreeNode) {
        val chunk = node.value as Chunk
        val entity = chunkMapper.mapToEntity(chunk)
        val id = chunkDao.insert(entity)
        entity.collectionFk = parentId
        chunkDao.update(entity)
    }
}