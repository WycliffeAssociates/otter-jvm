package persistence.repo

import data.Language
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


class LanguageRepo(private val dataStore: KotlinEntityDataStore<Persistable>): Dao<Language> {
    private val languageMapper = LanguageMapper()

    override fun delete(language: Language): Completable {
        return Completable.fromAction {
            dataStore.delete(ILanguageEntity::class).where(ILanguageEntity::id eq language.id).get().value()
        }
    }

    override fun getAll(): Observable<List<Language>> {
        val languageList = dataStore {
            val result = dataStore.select(ILanguageEntity::class)
            result.get().toList().asIterable()
        }
        return Observable.just(languageList.map { languageMapper.mapFromEntity(it) })
    }

    override fun getById(id: Int): Observable<Language> {
        val language = dataStore{
            val result = dataStore.select(ILanguageEntity::class).where(ILanguageEntity::id eq id)
            result.get().first()
        }
        return Observable.just(languageMapper.mapFromEntity(language))
    }

    override fun insert(language: Language): Observable<Int> {
        return Observable.just(dataStore.insert(languageMapper.mapToEntity(language)).id)
    }

    override fun update(language: Language): Completable {
        return Completable.fromAction {
            dataStore.update(ILanguageEntity::class)
                    .set(ILanguageEntity::slug, language.slug)
                    .set(ILanguageEntity::name, language.name)
                    .where(ILanguageEntity::id eq language.id)
        }
    }

}