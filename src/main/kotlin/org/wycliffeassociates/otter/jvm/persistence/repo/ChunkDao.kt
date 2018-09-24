package org.wycliffeassociates.otter.jvm.persistence.repo

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import jooq.tables.daos.ContentEntityDao
import org.wycliffeassociates.otter.common.data.dao.Dao
import org.wycliffeassociates.otter.common.data.model.Chunk
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.jvm.persistence.mapping.ChunkMapper

class ChunkDao(
        private val entityDao: ContentEntityDao,
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

}