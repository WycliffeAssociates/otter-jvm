package org.wycliffeassociates.otter.jvm.persistence.repo

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import jooq.tables.daos.ContentEntityDao
import org.wycliffeassociates.otter.common.data.dao.Dao
import org.wycliffeassociates.otter.common.data.model.Chunk
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.jvm.persistence.mapping.ChunkMapper

class ChunkDao(
        private val entityDao: ContentEntityDao,
        private val mapper: ChunkMapper
) : Dao<Chunk> {
    override fun delete(obj: Chunk): Completable {
        return Completable
                .fromAction {
                    entityDao.deleteById(obj.id)
                }.subscribeOn(Schedulers.io())
    }

    override fun getAll(): Observable<List<Chunk>> {
        return Observable
                .fromIterable(
                        entityDao
                                .findAll()
                                .map { mapper.mapFromEntity(Observable.just(it)) }
                )
                .flatMap { it }
                .toList()
                .toObservable()
                .subscribeOn(Schedulers.io())
    }

    override fun getById(id: Int): Observable<Chunk> {
        return Observable
                .fromCallable {
                    entityDao.fetchById(id).first()
                }
                .flatMap { mapper.mapFromEntity(Observable.just(it)) }
                .subscribeOn(Schedulers.io())
    }

    override fun insert(obj: Chunk): Observable<Int> {
        return Observable
                .fromCallable {
                    mapper.mapToEntity(Observable.just(obj))
                }
                .flatMap { it }
                .map { entity ->
                    if (entity.id == 0) entity.id = null
                    entityDao.insert(entity)
                    // Get the id
                    entityDao
                            .findAll()
                            .map { it.id }
                            .max() ?: 0
                }.subscribeOn(Schedulers.io())
    }

    fun insertForCollection(obj: Chunk, collection: Collection): Observable<Int> {
        return Observable
                .fromCallable {
                    mapper.mapToEntity(Observable.just(obj))
                }
                .flatMap { it }
                .map { entity ->
                    if (entity.id == 0) entity.id = null
                    entity.collectionFk = collection.id
                    entityDao.insert(entity)
                    // Get the id
                    entityDao
                            .findAll()
                            .map { it.id }
                            .max() ?: 0
                }.subscribeOn(Schedulers.io())
    }

    override fun update(obj: Chunk): Completable {
        return Completable
                .fromObservable(
                        mapper
                                .mapToEntity(Observable.just(obj))
                                .doOnNext {
                                    // Don't overwrite an existing relationship
                                    val existing = entityDao.fetchOneById(obj.id)
                                    it.collectionFk = existing.collectionFk
                                    entityDao.update(it)
                                }
                ).subscribeOn(Schedulers.io())
    }

    fun getChunksForCollection(collection: Collection): Observable<List<Chunk>> {
        return Observable
                .fromIterable(
                        entityDao
                                .fetchByCollectionFk(collection.id)
                                .map { mapper.mapFromEntity(Observable.just(it)) }
                )
                .flatMap { it }
                .toList()
                .toObservable()
                .subscribeOn(Schedulers.io())
    }

}