package org.wycliffeassociates.otter.jvm.persistence.repo

import org.wycliffeassociates.otter.common.data.model.Language
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.jooq.Configuration
import org.wycliffeassociates.otter.jvm.persistence.mapping.LanguageMapper
import jooq.tables.daos.LanguageEntityDao
import org.wycliffeassociates.otter.common.data.dao.Dao

class LanguageDao(
        private val entityDao: LanguageEntityDao,
        private val languageMapper: LanguageMapper
) : Dao<Language> {
    override fun delete(obj: Language): Completable {
        return Completable.fromAction {
            entityDao.delete(languageMapper.mapToEntity(obj))
        }.subscribeOn(Schedulers.io())
    }

    override fun getAll(): Observable<List<Language>> {
        return Observable.fromCallable {
            entityDao
                .findAll()
                .toList()
                .map { languageMapper.mapFromEntity(it) }
        }.subscribeOn(Schedulers.io())
    }

    override fun getById(id: Int): Observable<Language> {
        return Observable.fromCallable {
            languageMapper.mapFromEntity(
                    entityDao
                    .fetchById(id)
                    .first()
            )
        }.subscribeOn(Schedulers.io())
    }

    override fun insert(obj: Language): Observable<Int> {
        return Observable.fromCallable {
            val entity = languageMapper.mapToEntity(obj)
            if (entity.id == 0) entity.id = null
            entityDao.insert(entity)
            // fetches by slug to get inserted value since generated insert returns nothing
            entityDao
                .fetchBySlug(obj.slug)
                .first()
                .id
        }.subscribeOn(Schedulers.io())
    }

    override fun update(obj: Language): Completable {
        return Completable.fromAction {
            entityDao.update(languageMapper.mapToEntity(obj))
        }.subscribeOn(Schedulers.io())
    }
}