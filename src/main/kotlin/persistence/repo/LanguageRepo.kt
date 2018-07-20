package persistence.repo

import data.model.Language
import data.dao.Dao
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.requery.Persistable
import io.requery.kotlin.eq
import io.requery.sql.KotlinEntityDataStore
import persistence.mapping.LanguageMapper
import persistence.model.ILanguageEntity
import persistence.model.IUserLanguage


class LanguageRepo constructor(private val dataStore: KotlinEntityDataStore<Persistable>): Dao<Language> {
    val languageMapper = LanguageMapper()
    /**
     * given a language deletes the entry within the table
     */
    override fun delete(language: Language): Completable {
        return Completable.fromAction {
            dataStore.delete(IUserLanguage::class)
                        .where(IUserLanguage::languageEntityid eq language.id)
                        .get().value()
            dataStore.delete(ILanguageEntity::class)
                        .where(ILanguageEntity::id eq language.id)
                        .get().value()
        }.subscribeOn(Schedulers.io())
    }

    /**
     * gets all language entries and returns them as an observable language
     */
    override fun getAll(): Observable<List<Language>> {
        return Observable.create<List<ILanguageEntity>> {
            it.onNext(dataStore.select(ILanguageEntity::class)
                    .get().toList())
        }.subscribeOn(Schedulers.io())
        .map {
            it.map { languageMapper.mapFromEntity(it) }
        }
    }

    /**
     *  given an id gets and return a language observable
     */
    override fun getById(id: Int): Observable<Language> {
        return Observable.create<ILanguageEntity> {
            it.onNext(dataStore.select(ILanguageEntity::class)
                    .where(ILanguageEntity::id eq id)
                    .get().first())
        }.subscribeOn(Schedulers.io())
        .map {
            languageMapper.mapFromEntity(it)
        }
    }

    /**
     * given a language object inserts an entry
     * and returns the generated id as an observable
     */
    override fun insert(language: Language): Observable<Int> {
        return Observable.create<Int> {
            it.onNext(dataStore.insert(languageMapper.mapToEntity(language)).id)
        }.subscribeOn(Schedulers.io())
    }

    /**
     * given a language updates an entry
     * and returns a completable
     */
    override fun update(language: Language): Completable {
        return Completable.fromAction {
            dataStore.update(languageMapper.mapToEntity(language))
        }.subscribeOn(Schedulers.io())
    }

    /**
     * returns all source languages
     * as an observable list of languages
     */
    fun getGatewayLanguages(): Observable<List<Language>> {
        return Observable.create<List<ILanguageEntity>> {
            it.onNext(dataStore.select(ILanguageEntity::class)
                    .where(ILanguageEntity::gateway eq true)
                    .get().toList())
        }
        .subscribeOn(Schedulers.io())
        .map {
            it.map { languageMapper.mapFromEntity(it) }
        }
    }
}