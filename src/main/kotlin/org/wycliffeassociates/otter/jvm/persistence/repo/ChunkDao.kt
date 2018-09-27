package org.wycliffeassociates.otter.jvm.persistence.repo

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import jooq.tables.daos.ContentDerivativeDao
import jooq.tables.daos.ContentEntityDao
import jooq.tables.daos.ResourceLinkDao
import jooq.tables.pojos.ContentDerivative
import jooq.tables.pojos.ResourceLink
import org.wycliffeassociates.otter.common.data.model.Chunk
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.jvm.persistence.mapping.ChunkMapper

// Move to common
typealias Resource = Chunk

class ChunkDao(
        private val entityDao: ContentEntityDao,
        private val derivedDao: ContentDerivativeDao,
        private val resourceDao: ResourceLinkDao,
        private val mapper: ChunkMapper
) {
    fun delete(obj: Chunk): Completable {
        return Completable
                .fromAction {
                    entityDao.deleteById(obj.id)
                }.subscribeOn(Schedulers.io())
    }

    fun getAll(): Single<List<Chunk>> {
        return Observable
                .fromIterable(
                        entityDao
                                .findAll()
                                .map { mapper.mapFromEntity(Single.just(it)) }
                )
                .flatMap { it.toObservable() }
                .toList()
                .subscribeOn(Schedulers.io())
    }

    fun getById(id: Int): Maybe<Chunk> {
        return Maybe
                .fromCallable {
                    entityDao.fetchOneById(id)
                }
                .flatMap { mapper.mapFromEntity(Single.just(it)) }
                .onErrorComplete()
                .subscribeOn(Schedulers.io())
    }

    fun insertForCollection(obj: Chunk, collection: Collection): Single<Int> {
        return Single
                .fromCallable {
                    mapper.mapToEntity(Maybe.just(obj))
                }
                .flatMap { it }
                .map { entity ->
                    if (entity.id == 0) entity.id = null
                    entity.collectionFk = collection.id
                    entityDao.insert(entity)
                    // Get the id
                    obj.id = entityDao
                            .findAll()
                            .map { it.id }
                            .max() ?: 0
                    obj.id
                }.subscribeOn(Schedulers.io())
    }

    fun update(obj: Chunk, newCollection: Collection? = null): Completable {
        return mapper
                .mapToEntity(Maybe.just(obj))
                .doOnSuccess {
                    // Don't overwrite an existing relationship
                    val existing = entityDao.fetchOneById(obj.id)
                    it.collectionFk = newCollection?.id ?: existing.collectionFk
                    entityDao.update(it)
                }
                .toCompletable()
                .subscribeOn(Schedulers.io())
    }

    fun getByCollection(collection: Collection): Single<List<Chunk>> {
        return Observable
                .fromIterable(
                        entityDao
                                .fetchByCollectionFk(collection.id)
                                .map { mapper.mapFromEntity(Single.just(it)) }
                )
                .flatMap { it.toObservable() }
                .toList()
                .subscribeOn(Schedulers.io())
    }

    fun addSource(obj: Chunk, source: Chunk): Completable {
        // Query the derivative content table
        return Completable
                .fromAction {
                    // Try to insert the derived source row
                    val entity = ContentDerivative()
                    entity.sourceFk = source.id
                    entity.contentFk = obj.id
                    derivedDao.insert(entity)
                }
                .onErrorComplete() // Ignore errors if row already exists
                .subscribeOn(Schedulers.io())
    }

    fun getSources(obj: Chunk): Single<List<Chunk>> {
        // Query the derivative content table (autogen jooq dao)
        return Observable
                .fromIterable(
                    derivedDao
                            .fetchByContentFk(obj.id)
                            .map { mapper.mapFromEntity(Single.just(entityDao.fetchOneById(it.sourceFk))) }
                )
                .flatMap { it.toObservable() }
                .toList()
                .subscribeOn(Schedulers.io())
    }

    fun removeSource(obj: Chunk, source: Chunk): Completable {
        // Query the derivative content table
        return Completable
                .fromAction {
                    derivedDao.delete(derivedDao.fetchByContentFk(obj.id).filter { it.sourceFk == source.id })
                }
                .onErrorComplete() // Ignore errors if source chunk is not really source
                .subscribeOn(Schedulers.io())
    }

    // Resource interaction
    fun linkToResource(obj: Chunk, resource: Resource): Completable {
        return Completable
                .fromAction {
                    // Set up the entity to link the chunk and resource
                    val entity = ResourceLink()
                    entity.contentFk = obj.id
                    entity.resourceContentFk = resource.id
                    resourceDao.insert(entity)
                }
                .onErrorComplete() // Still complete if already exists in database
                .subscribeOn(Schedulers.io())
    }

    fun unlinkFromResource(obj: Chunk, resource: Resource): Completable {
        return Completable
                .fromAction {
                    // Remove existing link
                    resourceDao.delete(
                            resourceDao
                                    .fetchByResourceContentFk(resource.id)
                                    .filter { it.contentFk == obj.id }
                    )
                }
                .onErrorComplete() // Still complete if link does not exist in database
                .subscribeOn(Schedulers.io())
    }

    fun getResources(obj: Chunk): Single<List<Resource>> {
        return Observable
                .fromIterable(
                    resourceDao
                            .fetchByContentFk(obj.id)
                            .map { entityDao.fetchOneById(it.resourceContentFk) }
                            .map { mapper.mapFromEntity(Single.just(it)) }
                )
                .flatMap { it.toObservable() }
                .toList()
                .subscribeOn(Schedulers.io())
    }
}