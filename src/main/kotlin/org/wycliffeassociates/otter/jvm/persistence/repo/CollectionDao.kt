package org.wycliffeassociates.otter.jvm.persistence.repo

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import jooq.tables.daos.CollectionEntityDao
import org.wycliffeassociates.otter.common.data.model.Collection

import org.wycliffeassociates.otter.jvm.persistence.mapping.CollectionMapper


class CollectionDao(
        private val entityDao: CollectionEntityDao,
        private val mapper: CollectionMapper
) {
    fun delete(obj: Collection): Completable {
        return Completable
                .fromAction {
                    entityDao.deleteById(obj.id)
                }
                .subscribeOn(Schedulers.io())
    }

    fun getAll(): Single<List<Collection>> {
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

    fun getById(id: Int): Maybe<Collection> {
        return Maybe
                .fromCallable {
                    mapper.mapFromEntity(Single.just(entityDao.fetchOneById(id)))
                }
                .flatMap { it }
                .onErrorComplete()
                .subscribeOn(Schedulers.io())
    }

    fun insert(obj: Collection): Single<Int> {
        return insertRelated(obj)
    }

    fun insertRelated(
            obj: Collection,
            parent: Collection? = null,
            source: Collection? = null
    ): Single<Int> {
        return Single
                .fromCallable {
                    mapper.mapToEntity(Maybe.just(obj))
                }
                .flatMap { it }
                .map { entity ->
                    if (entity.id == 0) entity.id = null
                    parent?.let { entity.parentFk = parent.id }
                    source?.let { entity.sourceFk = source.id }
                    entity.rcFk = obj.resourceContainer.id
                    entityDao.insert(entity)
                    // Find the largest id (the latest)
                    obj.id = entityDao
                            .findAll()
                            .map {
                                it.id
                            }
                            .max() ?: 0 // Only null if nothing in database
                    obj.id
                }.subscribeOn(Schedulers.io())
    }

    fun update(obj: Collection): Completable {
        return mapper
                .mapToEntity(Maybe.just(obj))
                .doOnSuccess {
                    // Make sure we don't overwrite the existing parent and source keys
                    val existing = entityDao.fetchOneById(obj.id)
                    it.parentFk = existing.parentFk
                    it.sourceFk = existing.sourceFk
                    entityDao.update(it)
                }
                .toCompletable()
                .subscribeOn(Schedulers.io())
    }

    fun setParent(obj: Collection, parent: Collection?): Completable {
        return Completable
                .fromAction {
                    val entity = entityDao.fetchOneById(obj.id)
                    entity.parentFk = parent?.id
                    entityDao.update(entity)
                }.subscribeOn(Schedulers.io())
    }

    fun getChildren(obj: Collection): Single<List<Collection>> {
        return Observable
                .fromIterable(
                        entityDao
                            .fetchByParentFk(obj.id)
                            .toList()
                            .map { mapper.mapFromEntity(Single.just(it)) }
                )
                .flatMap { it.toObservable() }
                .toList()
                .subscribeOn(Schedulers.io())
    }

    fun setSource(obj: Collection, source: Collection?): Completable {
        return Completable
                .fromAction {
                    val entity = entityDao.fetchOneById(obj.id)
                    entity.sourceFk = source?.id
                    entityDao.update(entity)
                }.subscribeOn(Schedulers.io())
    }

    fun getSource(obj: Collection): Maybe<Collection> {
        return Maybe
                .fromCallable {
                    entityDao.fetchOneById(obj.id)
                }
                .filter { it.sourceFk != null }
                .flatMap {
                    mapper
                            .mapFromEntity(Single.just(entityDao.fetchOneById(it.sourceFk)))
                }
                .onErrorComplete()
                .subscribeOn(Schedulers.io())
    }

    fun getProjects(): Single<List<Collection>> {
        return Observable
                .fromIterable(
                    // Find all collections with no parent and some source
                    entityDao
                            .findAll()
                            .filter { it.sourceFk != null && it.parentFk == null}
                            .map { mapper.mapFromEntity(Single.just(it)) }
                )
                .flatMap { it.toObservable() }
                .toList()
                .subscribeOn(Schedulers.io())
    }

    fun getSources(): Single<List<Collection>> {
        return Observable
                .fromIterable(
                        entityDao
                                .findAll()
                                .filter { it.sourceFk == null && it.parentFk == null }
                                .map { mapper.mapFromEntity(Single.just(it)) }
                )
                .flatMap { it.toObservable() }
                .toList()
                .subscribeOn(Schedulers.io())
    }
}