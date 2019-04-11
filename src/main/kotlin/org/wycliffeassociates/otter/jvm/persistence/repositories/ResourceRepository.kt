package org.wycliffeassociates.otter.jvm.persistence.repositories

import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import jooq.Tables.CONTENT_ENTITY
import jooq.Tables.RESOURCE_LINK
import org.jooq.DSLContext
import org.wycliffeassociates.otter.common.collections.multimap.MultiMap
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Content
import org.wycliffeassociates.otter.common.persistence.repositories.IResourceRepository
import org.wycliffeassociates.otter.jvm.persistence.database.AppDatabase
import org.wycliffeassociates.otter.jvm.persistence.entities.CollectionEntity
import org.wycliffeassociates.otter.jvm.persistence.entities.ContentEntity
import org.wycliffeassociates.otter.jvm.persistence.entities.ResourceLinkEntity
import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.ContentMapper
import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.MarkerMapper
import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.TakeMapper

class ResourceRepository(
        val database: AppDatabase,
        private val contentMapper: ContentMapper = ContentMapper(),
        private val takeMapper: TakeMapper = TakeMapper(),
        private val markerMapper: MarkerMapper = MarkerMapper()
) : IResourceRepository {

    private val contentDao = database.getContentDao()
    private val collectionDao = database.getCollectionDao()
    private val takeDao = database.getTakeDao()
    private val markerDao = database.getMarkerDao()
    private val resourceLinkDao = database.getResourceLinkDao()
    private val subtreeHasResourceDao = database.getSubtreeHasResourceDao()

    override fun delete(obj: Content): Completable {
        return Completable
                .fromAction {
                    contentDao.delete(contentMapper.mapToEntity(obj))
                }
                .subscribeOn(Schedulers.io())
    }

    override fun getAll(): Single<List<Content>> {
        return Single
                .fromCallable {
                    contentDao
                            .fetchAll()
                            .map(this::buildResource)
                }
                .subscribeOn(Schedulers.io())
    }

    override fun getByCollection(collection: Collection): Single<List<Content>> {
        return Single
                .fromCallable {
                    resourceLinkDao
                            .fetchByCollectionId(collection.id)
                            .map {
                                contentDao.fetchById(it.resourceContentFk)
                            }
                            .map(this::buildResource)
                }
                .subscribeOn(Schedulers.io())
    }

    override fun getByContent(content: Content): Single<List<Content>> {
        return Single
                .fromCallable {
                    resourceLinkDao
                            .fetchByContentId(content.id)
                            .map {
                                contentDao.fetchById(it.resourceContentFk)
                            }
                            .map(this::buildResource)
                }
                .subscribeOn(Schedulers.io())
    }

    override fun linkToContent(resource: Content, content: Content, dublinCoreFk: Int): Completable {
        return Completable
                .fromAction {
                    // Check if already exists
                    val alreadyExists = resourceLinkDao
                            .fetchByContentId(content.id)
                            .any {
                                // Check for this link
                                it.resourceContentFk == resource.id
                            }

                    if (!alreadyExists) {
                        // Add the resource link
                        val entity = ResourceLinkEntity(
                                0,
                                resource.id,
                                content.id,
                                null,
                                dublinCoreFk
                        )
                        resourceLinkDao.insertNoReturn(entity)
                    }
                }
                .subscribeOn(Schedulers.io())
    }

    override fun linkToCollection(resource: Content, collection: Collection, dublinCoreFk: Int): Completable {
        return Completable
                .fromAction {
                    // Check if already exists
                    val alreadyExists = resourceLinkDao
                            .fetchByCollectionId(collection.id)
                            .filter {
                                // Check for this link
                                it.resourceContentFk == resource.id
                            }.isNotEmpty()

                    if (!alreadyExists) {
                        // Add the resource link
                        val entity = ResourceLinkEntity(
                                0,
                                resource.id,
                                null,
                                collection.id,
                                dublinCoreFk
                        )
                        resourceLinkDao.insertNoReturn(entity)
                    }
                }
                .subscribeOn(Schedulers.io())
    }

    override fun unlinkFromContent(resource: Content, content: Content): Completable {
        return Completable
                .fromAction {
                    // Check if exists
                    resourceLinkDao
                            .fetchByContentId(content.id)
                            .filter {
                                // Check for this link
                                it.resourceContentFk == resource.id
                            }
                            .forEach {
                                // Delete the link
                                resourceLinkDao.delete(it)
                            }
                }
                .subscribeOn(Schedulers.io())
    }

    override fun unlinkFromCollection(resource: Content, collection: Collection): Completable {
        return Completable
                .fromAction {
                    // Check if exists
                    resourceLinkDao
                            .fetchByCollectionId(collection.id)
                            .filter {
                                // Check for this link
                                it.resourceContentFk == resource.id
                            }
                            .forEach {
                                // Delete the link
                                resourceLinkDao.delete(it)
                            }
                }
                .subscribeOn(Schedulers.io())
    }

    override fun update(obj: Content): Completable {
        return Completable
                .fromAction {
                    val existing = contentDao.fetchById(obj.id)
                    val entity = contentMapper.mapToEntity(obj)
                    // Make sure we don't over write the collection relationship
                    entity.collectionFk = existing.collectionFk
                    contentDao.update(entity)
                }
                .subscribeOn(Schedulers.io())
    }

    override fun calculateAndSetSubtreeHasResources(collectionId: Int) {
        database.transaction { dsl ->
            val collectionEntity = collectionDao.fetchById(collectionId, dsl)
            val accumulator = MultiMap<Int, Int>()
            calculateAndSetSubtreeHasResources(collectionEntity, accumulator, dsl)
            subtreeHasResourceDao.insert(accumulator.kvSequence(), dsl)
        }
    }

    private fun calculateAndSetSubtreeHasResources(
        collection: CollectionEntity,
        collectToDublinId: MultiMap<Int, Int>,
        dsl: DSLContext
    ): Set<Int> {
        val childResources = collectionDao
            .fetchChildren(collection, dsl)
            .flatMap { calculateAndSetSubtreeHasResources(it, collectToDublinId, dsl) }
        val myCollectionResources = resourceLinkDao
            .fetchByCollectionId(collection.id, dsl)
            .map { it.dublinCoreFk }
        val myContentResources = getContentResourceFksByCollection(collection.id, dsl)
        val union = childResources
            .union(myCollectionResources)
            .union(myContentResources)

        union.forEach {
            collectToDublinId.put(collection.id, it)
        }

        return union
    }

    private fun getContentResourceFksByCollection(collectionId: Int, dsl: DSLContext): List<Int> {
        return dsl
            .selectDistinct(RESOURCE_LINK.DUBLIN_CORE_FK)
            .from(RESOURCE_LINK)
            .join(CONTENT_ENTITY)
            .on(RESOURCE_LINK.CONTENT_FK.eq(CONTENT_ENTITY.ID))
            .where(CONTENT_ENTITY.COLLECTION_FK.eq(collectionId))
            .fetch(RESOURCE_LINK.DUBLIN_CORE_FK)
    }

    private fun buildResource(entity: ContentEntity): Content {
        // Check for sources
        val sources = contentDao.fetchSources(entity)
        val contentEnd = sources.map { it.start }.max() ?: entity.start
        val selectedTake = entity
                .selectedTakeFk?.let { selectedTakeFk ->
            // Retrieve the markers
            val markers = markerDao
                    .fetchByTakeId(selectedTakeFk)
                    .map(markerMapper::mapFromEntity)
            takeMapper.mapFromEntity(takeDao.fetchById(selectedTakeFk), markers)
        }
        return contentMapper.mapFromEntity(entity, selectedTake, contentEnd)
    }

}