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
    private val database: AppDatabase,
    private val contentMapper: ContentMapper = ContentMapper(),
    private val takeMapper: TakeMapper = TakeMapper(),
    private val markerMapper: MarkerMapper = MarkerMapper()
) : IResourceRepository {

    private val contentDao = database.contentDao
    private val collectionDao = database.collectionDao
    private val takeDao = database.takeDao
    private val markerDao = database.markerDao
    private val resourceLinkDao = database.resourceLinkDao
    private val subtreeHasResourceDao = database.subtreeHasResourceDao

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

    private fun insert(entity: ResourceLinkEntity): Completable {
        return Completable
            .fromAction {
                resourceLinkDao.insertNoReturn(entity)
            }
            .subscribeOn(Schedulers.io())
    }

    override fun linkToContent(resource: Content, content: Content, dublinCoreFk: Int) = insert(
        ResourceLinkEntity(
            id = 0,
            resourceContentFk = resource.id,
            contentFk = content.id,
            collectionFk = null,
            dublinCoreFk = dublinCoreFk
        )
    )

    override fun linkToCollection(resource: Content, collection: Collection, dublinCoreFk: Int) = insert(
        ResourceLinkEntity(
            id = 0,
            resourceContentFk = resource.id,
            contentFk = null,
            collectionFk = collection.id,
            dublinCoreFk = dublinCoreFk
        )
    )

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
        mMapCollectionToDublinId: MultiMap<Int, Int>,
        dsl: DSLContext
    ): Set<Int> {
        val childResources = collectionDao
            .fetchChildren(collection, dsl)
            .flatMap { calculateAndSetSubtreeHasResources(it, mMapCollectionToDublinId, dsl) }
        val myCollectionResources = resourceLinkDao
            .fetchByCollectionId(collection.id, dsl)
            .map { it.dublinCoreFk }
        val myContentResources = getContentResourceFksByCollection(collection.id, dsl)
        val union = childResources
            .union(myCollectionResources)
            .union(myContentResources)

        union.forEach {
            mMapCollectionToDublinId[collection.id] = it
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