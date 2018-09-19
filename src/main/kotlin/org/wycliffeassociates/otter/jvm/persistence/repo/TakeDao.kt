package org.wycliffeassociates.otter.jvm.persistence.repo

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import jooq.tables.daos.TakeEntityDao
import org.wycliffeassociates.otter.common.data.dao.Dao
import org.wycliffeassociates.otter.common.data.model.Chunk
import org.wycliffeassociates.otter.common.data.model.Take
import org.wycliffeassociates.otter.jvm.persistence.mapping.TakeMapper

class TakeDao(
        private val entityDao: TakeEntityDao,
        private val mapper: TakeMapper
) : Dao<Take> {
    override fun delete(obj: Take): Completable {
        return Completable
                .fromAction {
                    entityDao.deleteById(obj.id)
                }.subscribeOn(Schedulers.io())
    }

    override fun getAll(): Observable<List<Take>> {
        return Observable
                .fromCallable {
                    entityDao
                            .findAll()
                            .map { mapper.mapFromEntity(it) }
                }.subscribeOn(Schedulers.io())
    }

    override fun getById(id: Int): Observable<Take> {
        return Observable
                .fromCallable {
                    mapper.mapFromEntity(entityDao.fetchById(id).first())
                }
                .subscribeOn(Schedulers.io())
    }

    override fun insert(obj: Take): Observable<Int> {
        return Observable
                .fromCallable {
                    val entity = mapper.mapToEntity(obj)
                    if (entity.id == 0) entity.id = null
                    entityDao.insert(entity)
                    // Get the id
                    entityDao
                            .findAll()
                            .map { it.id }
                            .max() ?: 0
                }.subscribeOn(Schedulers.io())
    }

    fun insertForChunk(obj: Take, chunk: Chunk): Observable<Int> {
        return Observable
                .fromCallable {
                    val entity = mapper.mapToEntity(obj)
                    if (entity.id == 0) entity.id = null
                    // Set the relationship
                    entity.contentFk = chunk.id
                    entityDao.insert(entity)
                    // Get the id
                    entityDao
                            .findAll()
                            .map { it.id }
                            .max() ?: 0
                }.subscribeOn(Schedulers.io())
    }

    override fun update(obj: Take): Completable {
        return Completable
                .fromAction {
                    val entity = mapper.mapToEntity(obj)
                    // Don't overwrite an existing relationship
                    val existing = entityDao.fetchOneById(obj.id)
                    entity.contentFk = existing.contentFk
                    entityDao.update(entity)
                }.subscribeOn(Schedulers.io())
    }

    fun getTakesForChunk(chunk: Chunk): Observable<List<Take>> {
        return Observable
                .fromCallable {
                    entityDao
                            .fetchByContentFk(chunk.id)
                            .map { mapper.mapFromEntity(it) }
                }.subscribeOn(Schedulers.io())
    }

}