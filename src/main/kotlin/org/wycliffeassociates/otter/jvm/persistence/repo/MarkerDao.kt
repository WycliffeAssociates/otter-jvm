package org.wycliffeassociates.otter.jvm.persistence.repo

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import jooq.tables.daos.MarkerEntityDao
import org.wycliffeassociates.otter.common.data.model.Marker
import org.wycliffeassociates.otter.common.data.model.Take
import org.wycliffeassociates.otter.jvm.persistence.mapping.MarkerMapper

class MarkerDao(
        private val entityDao: MarkerEntityDao,
        private val mapper: MarkerMapper
) {
    fun delete(obj: Marker): Completable {
        return Completable
                .fromAction {
                    entityDao.deleteById(obj.id)
                }.subscribeOn(Schedulers.io())
    }

    fun getAll(): Single<List<Marker>> {
        return Single
                .fromCallable {
                    entityDao
                            .findAll()
                            .map { mapper.mapFromEntity(it) }
                }.subscribeOn(Schedulers.io())
    }

    fun getById(id: Int): Maybe<Marker> {
        return Maybe
                .fromCallable {
                    mapper.mapFromEntity(entityDao.fetchById(id).first())
                }
                .onErrorComplete()
                .subscribeOn(Schedulers.io())
    }

    fun insertForTake(obj: Marker, take: Take): Single<Int> {
        return Single
                .fromCallable {
                    val entity = mapper.mapToEntity(obj)
                    if (entity.id == 0) entity.id = null
                    // Set the relationship
                    entity.takeFk = take.id
                    entityDao.insert(entity)
                    // Get the id
                    obj.id = entityDao
                            .findAll()
                            .map { it.id }
                            .max() ?: 0
                    obj.id
                }.subscribeOn(Schedulers.io())
    }

    fun update(obj: Marker, newTake: Take? = null): Completable {
        return Completable
                .fromAction {
                    val entity = mapper.mapToEntity(obj)
                    // Don't overwrite an existing relationship
                    val existing = entityDao.fetchOneById(obj.id)
                    entity.takeFk = newTake?.id ?: existing.takeFk
                    entityDao.update(entity)
                }.subscribeOn(Schedulers.io())
    }

    fun getByTake(take: Take): Single<List<Marker>> {
        return Single
                .fromCallable {
                    entityDao
                            .fetchByTakeFk(take.id)
                            .map { mapper.mapFromEntity(it) }
                }.subscribeOn(Schedulers.io())
    }
}