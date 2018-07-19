package persistence.repo

import data.Language
import data.User
import data.dao.Dao
import io.reactivex.Completable
import io.reactivex.Observable
import io.requery.Persistable
import io.requery.kotlin.eq
import io.requery.kotlin.set
import io.requery.sql.KotlinEntityDataStore
import persistence.mapping.LanguageMapper
import persistence.model.ILanguageEntity
import persistence.model.IUserEntity
import persistence.model.IUserLanguage
import javax.inject.Inject


class LanguageRepo constructor(private val dataStore: KotlinEntityDataStore<Persistable>): Dao<Language> {
    val languageMapper = LanguageMapper()
    /**
     * given a language deletes the entry within the table
     */
    override fun delete(language: Language): Completable {
        return Completable.fromAction {
            dataStore.delete(IUserLanguage::class).where(IUserLanguage::languageEntityid eq language.id).get().value()
            dataStore.delete(ILanguageEntity::class).where(ILanguageEntity::id eq language.id).get().value()
        }
    }

    /**
     * gets all language entries and returns them as an observable language
     */
    override fun getAll(): Observable<List<Language>> {
        val languageList = dataStore.select(ILanguageEntity::class).get().toList()

        return Observable.just(languageList.map { languageMapper.mapFromEntity(it) })
    }

    /**
     *  given an id gets and return a language observable
     */
    override fun getById(id: Int): Observable<Language> {
        val language = dataStore.invoke {
            val result = dataStore.select(ILanguageEntity::class).where(ILanguageEntity::id eq id)
            result.get().first()
        }
        return Observable.just(languageMapper.mapFromEntity(language))
    }

    /**
     * given a language object inserts an entry
     * and returns the generated id as an observable
     */
    override fun insert(language: Language): Observable<Int> {
        return Observable.just(dataStore.insert(languageMapper.mapToEntity(language)).id)
    }

    /**
     * given a language updates an entry
     * and returns a completable
     */
    override fun update(language: Language): Completable {
        return Completable.fromAction {
            dataStore.update(ILanguageEntity::class)
                    .set(ILanguageEntity::slug, language.slug)
                    .set(ILanguageEntity::name, language.name)
                    .set(ILanguageEntity::canBeSource, language.canBeSource)
                    .set(ILanguageEntity::anglicizedName, language.anglicizedName)
                    .where(ILanguageEntity::id eq language.id).get().value()
        }
    }

    /**
     * returns all source languages
     * as an observable list of languages
     */
    fun getSourceLanguages(): Observable<List<Language>> {
        val languageList = dataStore.select(ILanguageEntity::class)
                .where(ILanguageEntity::canBeSource eq true)
                .get()
                .toList()
                .asIterable()

        return Observable.just(languageList.map { languageMapper.mapFromEntity(it) })
    }
}