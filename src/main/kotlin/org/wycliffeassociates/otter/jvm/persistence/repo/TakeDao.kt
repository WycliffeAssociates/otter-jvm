package org.wycliffeassociates.otter.jvm.persistence.repo

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import jooq.tables.daos.TakeEntityDao
import org.wycliffeassociates.otter.common.data.model.Chunk
import org.wycliffeassociates.otter.common.data.model.Take
import org.wycliffeassociates.otter.jvm.persistence.mapping.TakeMapper

class TakeDao(
        private val entityDao: TakeEntityDao,
        private val markerDao: MarkerDao,
        private val mapper: TakeMapper
) {
    fun delete(obj: Take): Completable {
        return Completable
                .fromAction {
                    entityDao.deleteById(obj.id)
                }.subscribeOn(Schedulers.io())
    }

    fun getAll(): Single<List<Take>> {
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

    fun getById(id: Int): Maybe<Take> {
        return Maybe
                .fromCallable {
                    mapper.mapFromEntity(Single.just(entityDao.fetchById(id).first()))
                }
                .flatMap { it.toMaybe() }
                .onErrorComplete()
                .subscribeOn(Schedulers.io())
    }

    fun insertForChunk(obj: Take, chunk: Chunk): Single<Int> {
        return mapper
                .mapToEntity(Single.just(obj))
                .map { entity ->
                    if (entity.id == 0) entity.id = null
                    // Set the relationship
                    entity.contentFk = chunk.id
                    entityDao.insert(entity)
                    // Get the id
                    obj.id = entityDao
                            .findAll()
                            .map { it.id }
                            .max() ?: 0
                    obj.id
                }
                // Insert the takes
                .map {
                    obj.markers.map { marker ->
                        markerDao.insertForTake(marker, obj)
                    }
                }
                .toObservable()
                .flatMap { Observable.fromIterable(it) }
                .flatMap { it.toObservable() }
                .toList()
                .map {
                    obj.id
                }
                .subscribeOn(Schedulers.io())
    }

    fun update(obj: Take, newChunk: Chunk? = null): Completable {
        return mapper
                .mapToEntity(Single.just(obj))
                .map { entity ->
                    // Don't overwrite an existing relationship
                    val existing = entityDao.fetchOneById(obj.id)
                    entity.contentFk = newChunk?.id ?: existing.contentFk
                    entityDao.update(entity)
                }
                .toCompletable()
                .subscribeOn(Schedulers.io())
    }

    fun getByChunk(chunk: Chunk): Single<List<Take>> {
        return Observable
                .fromIterable(
                    entityDao
                            .fetchByContentFk(chunk.id)
                            .map { mapper.mapFromEntity(Single.just(it)) }
                )
                .flatMap { it.toObservable() }
                .toList()
                .subscribeOn(Schedulers.io())
    }

}