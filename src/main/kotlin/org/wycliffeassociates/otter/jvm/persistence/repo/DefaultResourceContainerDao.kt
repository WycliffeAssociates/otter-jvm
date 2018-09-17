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
        config: Configuration,
        private val mapper: ResourceContainerMapper
) : Dao<ResourceContainer> {
    private val dublinCoreDao = DublinCoreEntityDao(config)
    override fun delete(obj: ResourceContainer): Completable {
        return Completable
                .fromAction {
                    dublinCoreDao.deleteById(obj.id)
                }
                .subscribeOn(Schedulers.io())
    }

    override fun getAll(): Observable<List<ResourceContainer>> {
        return Observable
                .fromIterable(
                    dublinCoreDao
                            .findAll()
                            .toList()
                            .map { mapper.mapFromEntity(Observable.just(it)) }
                )
                // Unwrap the observable containers from the mapper
                .flatMap {
                    it
                }
                // Aggregate back to list
                .toList()
                .toObservable()
                .subscribeOn(Schedulers.io())
    }

    override fun getById(id: Int): Observable<ResourceContainer> {
        return Observable
                .fromCallable {
                    mapper.mapFromEntity(Observable.just(dublinCoreDao.fetchById(id).first()))
                }
                .flatMap {
                    it
                }
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
                    dublinCoreDao.insert(entity)
                    // Find the largest id (the latest)
                    dublinCoreDao
                            .findAll()
                            .map {
                                it.id
                            }
                            .max()
                }
    }

    override fun update(obj: ResourceContainer): Completable {
        return Completable.fromObservable(
                mapper
                        .mapToEntity(Observable.just(obj))
                        .doOnNext {
                            dublinCoreDao.update(it)
                        }
        )
    }

}