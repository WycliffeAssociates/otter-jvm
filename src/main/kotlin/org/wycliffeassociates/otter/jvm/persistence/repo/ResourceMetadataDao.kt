package org.wycliffeassociates.otter.jvm.persistence.repo

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import jooq.tables.pojos.RcLinkEntity
import jooq.tables.daos.DublinCoreEntityDao
import jooq.tables.daos.RcLinkEntityDao
import org.wycliffeassociates.otter.common.data.model.ResourceMetadata
import org.wycliffeassociates.otter.jvm.persistence.mapping.ResourceMetadataMapper

class ResourceMetadataDao(
        private val entityDao: DublinCoreEntityDao,
        private val linkDao: RcLinkEntityDao,
        private val mapper: ResourceMetadataMapper
) {
    fun delete(obj: ResourceMetadata): Completable {
        return Completable
                .fromAction {
                    entityDao.deleteById(obj.id)
                }.subscribeOn(Schedulers.io())
    }

    fun getAll(): Single<List<ResourceMetadata>> {
        return Observable
                .fromIterable(
                        entityDao
                            .findAll()
                            .toList()
                            .map { mapper.mapFromEntity(Single.just(it)) }
                )
                // Unwrap the observable containers from the mapper
                .flatMap { it.toObservable() }
                // Aggregate back to list
                .toList()
                .subscribeOn(Schedulers.io())
    }

    fun getById(id: Int): Maybe<ResourceMetadata> {
        return Maybe
                .fromCallable {
                    mapper.mapFromEntity(Single.just(entityDao.fetchOneById(id)))
                }
                .flatMap { it }
                .onErrorComplete()
                .subscribeOn(Schedulers.io())
    }

    fun insert(obj: ResourceMetadata): Single<Int> {
        return Single
                .fromCallable {
                    mapper.mapToEntity(Maybe.just(obj))
                }
                .flatMap { it }
                .map { entity ->
                    if (entity.id == 0) entity.id = null
                    entityDao.insert(entity)
                    // Find the largest id (the latest)
                    obj.id = entityDao
                            .findAll()
                            .map {
                                it.id
                            }
                            .max() ?: 0 // Only zero if db is empty
                    obj.id
                }
                .subscribeOn(Schedulers.io())
    }

    fun update(obj: ResourceMetadata): Completable {
        return mapper
                .mapToEntity(Maybe.just(obj))
                .doOnSuccess {
                    entityDao.update(it)
                }
                .toCompletable()
                .subscribeOn(Schedulers.io())
    }

    fun getLinks(obj: ResourceMetadata): Single<List<ResourceMetadata>> {
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
                            .map { mapper.mapFromEntity(Single.just(it)) }
                }
                .flatMap { Observable.fromIterable(it) }
                .flatMap { it.toObservable() }
                .toList()
                .subscribeOn(Schedulers.io())
    }

    // This function is commutative
    fun addLink(rc1: ResourceMetadata, rc2: ResourceMetadata): Completable {
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
    fun removeLink(rc1: ResourceMetadata, rc2: ResourceMetadata): Completable {
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