package org.wycliffeassociates.otter.jvm.persistence.repo

import org.wycliffeassociates.otter.common.data.model.Language
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.wycliffeassociates.otter.jvm.persistence.mapping.LanguageMapper
import jooq.tables.daos.LanguageEntityDao
import org.wycliffeassociates.otter.common.persistence.repositories.ILanguageRepository

class LanguageDao(
        private val entityDao: LanguageEntityDao,
        private val languageMapper: LanguageMapper
) : ILanguageRepository {
    override fun getGateway(): Single<List<Language>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTargets(): Single<List<Language>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(obj: Language): Completable {
        return Completable.fromAction {
            entityDao.deleteById(obj.id)
        }.subscribeOn(Schedulers.io())
    }

    override fun getAll(): Single<List<Language>> {
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
                                    .fetchOneById(id)
                    )
                }
                .onErrorComplete() // no success if no language, but still completes
                .subscribeOn(Schedulers.io())
    }

    override fun getBySlug(slug: String): Single<Language> {
        return Single
                .fromCallable {
                    languageMapper.mapFromEntity(
                            entityDao
                                    .fetchBySlug(slug)
                                    .first()
                    )
                }
                .onErrorReturn { throw  it }
                .subscribeOn(Schedulers.io())
    }

   override fun insert(obj: Language): Single<Int> {
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

    override fun update(obj: Language): Completable {
        return Completable.fromAction {
            entityDao.update(languageMapper.mapToEntity(obj))
        }.subscribeOn(Schedulers.io())
    }
}