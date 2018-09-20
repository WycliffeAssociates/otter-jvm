package org.wycliffeassociates.otter.jvm.persistence.repo

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import jooq.tables.daos.TakeEntityDao
import org.wycliffeassociates.otter.common.data.model.Chunk
import org.wycliffeassociates.otter.common.data.model.Take
import org.wycliffeassociates.otter.jvm.persistence.mapping.TakeMapper

class TakeDao(
        private val entityDao: TakeEntityDao,
        private val mapper: TakeMapper
) {
    fun delete(obj: Take): Completable {
        return Completable
                .fromAction {
                    entityDao.deleteById(obj.id)
                }.subscribeOn(Schedulers.io())
    }

    fun getAll(): Single<List<Take>> {
        return Single
                .fromCallable {
                    entityDao
                            .findAll()
                            .map { mapper.mapFromEntity(it) }
                }.subscribeOn(Schedulers.io())
    }

    fun getById(id: Int): Maybe<Take> {
        return Maybe
                .fromCallable {
                    mapper.mapFromEntity(entityDao.fetchById(id).first())
                }
                .onErrorComplete()
                .subscribeOn(Schedulers.io())
    }

    fun insertForChunk(obj: Take, chunk: Chunk): Single<Int> {
        return Single
                .fromCallable {
                    val entity = mapper.mapToEntity(obj)
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
                }.subscribeOn(Schedulers.io())
    }

    fun update(obj: Take, newChunk: Chunk? = null): Completable {
        return Completable
                .fromAction {
                    val entity = mapper.mapToEntity(obj)
                    // Don't overwrite an existing relationship
                    val existing = entityDao.fetchOneById(obj.id)
                    entity.contentFk = newChunk?.id ?: existing.contentFk
                    entityDao.update(entity)
                }.subscribeOn(Schedulers.io())
    }

    fun getByChunk(chunk: Chunk): Single<List<Take>> {
        return Single
                .fromCallable {
                    entityDao
                            .fetchByContentFk(chunk.id)
                            .map { mapper.mapFromEntity(it) }
                }.subscribeOn(Schedulers.io())
    }

}