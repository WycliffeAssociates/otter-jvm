package org.wycliffeassociates.otter.jvm.persistence.repo

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import jooq.tables.pojos.RcLinkEntity
import jooq.tables.daos.DublinCoreEntityDao
import jooq.tables.daos.RcLinkEntityDao
import org.wycliffeassociates.otter.common.data.dao.Dao
import org.wycliffeassociates.otter.common.data.model.ResourceContainer
import org.wycliffeassociates.otter.jvm.persistence.mapping.ResourceContainerMapper

class DefaultResourceContainerDao(
        private val entityDao: DublinCoreEntityDao,
        private val linkDao: RcLinkEntityDao,
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

    fun getLinks(obj: ResourceContainer): Observable<List<ResourceContainer>> {
        return Observable
                .fromCallable {
                    // Get links where the obj is RC1
                    val rcIsRC1Links = linkDao
                            .fetchByRc1Fk(obj.id)
                            .map { entityDao.fetchOneById(it.rc2Fk) }
                    // Get links where the obj is RC2
                    val rcIsRC2Links = linkDao
                            .fetchByRc2Fk(obj.id)
                            .map { entityDao.fetchOneById(it.rc1Fk) }
                    rcIsRC1Links
                            .union(rcIsRC2Links)
                            .map { mapper.mapFromEntity(Observable.just(it)) }
                }
                .flatMap { Observable.fromIterable(it) }
                .flatMap { it }
                .toList()
                .toObservable()
                .subscribeOn(Schedulers.io())
    }

    // This function is commutative
    fun addLink(rc1: ResourceContainer, rc2: ResourceContainer): Completable {
        return Completable
                .fromAction {
                    // Sort so rc1 is always < rc2
                    // Prevents double insertion of link
                    val entity = RcLinkEntity(
                            if (rc1.id < rc2.id) rc1.id else rc2.id,
                            if (rc2.id > rc1.id) rc2.id else rc1.id
                    )
                    linkDao.insert(entity)
                }
                // Ignore error if link already in database
                .onErrorComplete()
                .subscribeOn(Schedulers.io())
    }

    // This function is commutative
    fun removeLink(rc1: ResourceContainer, rc2: ResourceContainer): Completable {
        return Completable
                .fromAction {
                    val rc1Fk = if (rc1.id < rc2.id) rc1.id else rc2.id
                    val rc2Fk = if (rc2.id > rc1.id) rc2.id else rc1.id
                    linkDao
                            .fetchByRc1Fk(rc1Fk)
                            .filter { it.rc2Fk == rc2Fk }
                            .forEach { linkDao.delete(it) }
                }.subscribeOn(Schedulers.io())
    }
}