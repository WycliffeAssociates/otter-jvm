package org.wycliffeassociates.otter.jvm.persistence.repo

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import jooq.tables.daos.DublinCoreEntityDao
import org.jooq.Configuration
import org.wycliffeassociates.otter.common.data.dao.Dao
import org.wycliffeassociates.otter.common.data.model.ResourceContainer
import org.wycliffeassociates.otter.jvm.persistence.mapping.ResourceContainerMapper

class DefaultResourceContainerDao(
        private val entityDao: DublinCoreEntityDao,
        private val mapper: ResourceContainerMapper
) : Dao<ResourceContainer> {
    override fun delete(obj: ResourceContainer): Completable {
        return Completable
                .fromAction {
                    entityDao.deleteById(obj.id)
                }.subscribeOn(Schedulers.io())
    }

    override fun getAll(): Observable<List<ResourceContainer>> {
        return Observable
                .fromIterable(
                        entityDao
                            .findAll()
                            .toList()
                            .map { mapper.mapFromEntity(Observable.just(it)) }
                )
                // Unwrap the observable containers from the mapper
                .flatMap { it }
                // Aggregate back to list
                .toList()
                .toObservable()
                .subscribeOn(Schedulers.io())
    }

    override fun getById(id: Int): Observable<ResourceContainer> {
        return Observable
                .fromCallable {
                    mapper.mapFromEntity(Observable.just(entityDao.fetchById(id).first()))
                }
                .flatMap { it }
                .subscribeOn(Schedulers.io())
    }

    override fun insert(obj: ResourceContainer): Observable<Int> {
        return Observable
                .fromCallable {
                    mapper.mapToEntity(Observable.just(obj))
                }
                .flatMap {
                    it
                }
                .map { entity ->
                    if (entity.id == 0) entity.id = null
                    entityDao.insert(entity)
                    // Find the largest id (the latest)
                    entityDao
                            .findAll()
                            .map {
                                it.id
                            }
                            .max() ?: 0 // Only zero if db is empty
                }
                .subscribeOn(Schedulers.io())
    }

    override fun update(obj: ResourceContainer): Completable {
        return Completable
                .fromObservable(
                        mapper
                                .mapToEntity(Observable.just(obj))
                                .doOnNext {
                                    entityDao.update(it)
                                }
                ).subscribeOn(Schedulers.io())
    }
}