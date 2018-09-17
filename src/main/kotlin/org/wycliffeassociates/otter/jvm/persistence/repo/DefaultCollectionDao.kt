package org.wycliffeassociates.otter.jvm.persistence.repo

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import jooq.tables.daos.CollectionEntityDao
import org.jooq.Configuration
import org.wycliffeassociates.otter.common.data.dao.Dao
import org.wycliffeassociates.otter.common.data.model.Collection

import org.wycliffeassociates.otter.jvm.persistence.mapping.CollectionMapper


class DefaultCollectionDao(
        config: Configuration,
        private val mapper: CollectionMapper
) : Dao<Collection> {
    private val collectionEntityDao = CollectionEntityDao(config)
    override fun delete(obj: Collection): Completable {
        return Completable
                .fromAction {
                    collectionEntityDao.deleteById(obj.id)
                }
                .subscribeOn(Schedulers.io())
    }

    override fun getAll(): Observable<List<Collection>> {
        return Observable
                .fromIterable(
                        collectionEntityDao
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

    override fun getById(id: Int): Observable<Collection> {
        return Observable
                .fromCallable {
                    mapper.mapFromEntity(Observable.just(collectionEntityDao.fetchById(id).first()))
                }
                .flatMap {
                    it
                }
                .subscribeOn(Schedulers.io())
    }

    override fun insert(obj: Collection): Observable<Int> {
        return insertRelated(obj)
    }

    fun insertRelated(
            obj: Collection,
            parent: Collection? = null,
            source: Collection? = null
    ): Observable<Int> {
        return Observable
                .fromCallable {
                    mapper.mapToEntity(Observable.just(obj))
                }
                .flatMap {
                    it
                }
                .map { entity ->
                    if (entity.id == 0) entity.id = null
                    parent?.let { entity.parentFk = parent.id }
                    source?.let { entity.sourceFk = source.id }
                    entity.rcFk = obj.resourceContainer.id
                    collectionEntityDao.insert(entity)
                    // Find the largest id (the latest)
                    collectionEntityDao
                            .findAll()
                            .map {
                                it.id
                            }
                            .max()
                }
    }

    override fun update(obj: Collection): Completable {
        return Completable.fromObservable(
                mapper
                        .mapToEntity(Observable.just(obj))
                        .doOnNext {
                            // Make sure we don't overwrite the existing parent and source keys
                            val existing = collectionEntityDao.fetchOneById(obj.id)
                            it.parentFk = existing.parentFk
                            it.sourceFk = existing.sourceFk
                            collectionEntityDao.update(it)
                        }
        )
    }

    fun setParent(obj: Collection, parent: Collection?): Completable {
        return Completable.fromAction {
            val entity = collectionEntityDao.fetchOneById(obj.id)
            entity.parentFk = parent?.id
            collectionEntityDao.update(entity)
        }
    }

    fun getChildren(obj: Collection): Observable<List<Collection>> {
        return Observable
                .fromIterable(
                    collectionEntityDao
                            .fetchByParentFk(obj.id)
                            .toList()
                            .map { mapper.mapFromEntity(Observable.just(it)) }
                )
                .flatMap {
                    it
                }
                .toList()
                .toObservable()
    }

    fun setSource(obj: Collection, source: Collection?): Completable {
        return Completable.fromAction {
            val entity = collectionEntityDao.fetchOneById(obj.id)
            entity.sourceFk = source?.id
            collectionEntityDao.update(entity)
        }
    }

    fun getSource(obj: Collection): Observable<Collection> {
        return Observable
                .fromCallable {
                    val entity = collectionEntityDao.fetchOneById(obj.id)
                    if (entity.sourceFk != null) {
                        val source = collectionEntityDao.fetchOneById(entity.sourceFk)
                        mapper.mapFromEntity(Observable.just(source))
                    } else {
                        Observable.empty()
                    }
                }
                .flatMap {
                    it
                }
    }

}