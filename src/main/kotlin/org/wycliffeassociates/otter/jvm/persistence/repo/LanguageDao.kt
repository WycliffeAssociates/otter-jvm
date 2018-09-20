package org.wycliffeassociates.otter.jvm.persistence.repo

import org.wycliffeassociates.otter.common.data.model.Language
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.wycliffeassociates.otter.jvm.persistence.mapping.LanguageMapper
import jooq.tables.daos.LanguageEntityDao
import org.wycliffeassociates.otter.common.data.dao.Dao

class LanguageDao(
        private val entityDao: LanguageEntityDao,
        private val languageMapper: LanguageMapper
) {
    fun delete(obj: Language): Completable {
        return Completable.fromAction {
            entityDao.delete(languageMapper.mapToEntity(obj))
        }.subscribeOn(Schedulers.io())
    }

    fun getAll(): Single<List<Language>> {
        return Single.fromCallable {
            entityDao
                .findAll()
                .toList()
                .map { languageMapper.mapFromEntity(it) }
        }.subscribeOn(Schedulers.io())
    }

    fun getById(id: Int): Maybe<Language> {
        return Maybe
                .fromCallable {
                    languageMapper.mapFromEntity(
                            entityDao
                            .fetchById(id)
                            .first()
                    )
                }
                .onErrorComplete() // no success if no language, but still completes
                .subscribeOn(Schedulers.io())
    }

    fun getBySlug(slug: String): Maybe<Language> {
        return Maybe
                .fromCallable {
                    languageMapper.mapFromEntity(
                            entityDao
                                    .fetchBySlug(slug)
                                    .first()
                    )
                }
                .onErrorComplete()
                .subscribeOn(Schedulers.io())
    }

    fun insert(obj: Language): Single<Int> {
        return Single.fromCallable {
            val entity = languageMapper.mapToEntity(obj)
            if (entity.id == 0) entity.id = null
            entityDao.insert(entity)
            // fetches by slug to get inserted value since generated insert returns nothing
            obj.id = entityDao
                .fetchBySlug(obj.slug)
                .first()
                .id
            obj.id
        }.subscribeOn(Schedulers.io())
    }

    fun update(obj: Language): Completable {
        return Completable.fromAction {
            entityDao.update(languageMapper.mapToEntity(obj))
        }.subscribeOn(Schedulers.io())
    }
}