package persistence.mapping

import data.model.Language
import data.model.UserPreferences
import data.dao.Dao
import data.mapping.Mapper
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import org.reactfx.util.TriFunction
import persistence.model.IUserPreferencesEntity
import persistence.model.UserPreferencesEntity

class UserPreferencesMapper(private val languageRepo: Dao<Language>):
        Mapper<Observable<IUserPreferencesEntity>, Observable<UserPreferences>> {

    /**
     * Takes an Observable IUserPreferencesEntity and maps and returns an Observable UserPreferences object
     */
    override fun mapFromEntity(type: Observable<IUserPreferencesEntity>): Observable<UserPreferences> {
        // gets from database and maps preferred source and target language

        return type.flatMap {
            val preferredSourceLanguage = languageRepo.getById(it.sourceLanguageId)
            val preferredTargetLanguage = languageRepo.getById(it.targetLanguageId)
            Observable.zip(preferredSourceLanguage, preferredTargetLanguage,
                    BiFunction<Language, Language, UserPreferences>
                            {source, target  -> UserPreferences(it.id, source, target)})
        }
    }

    /**
     * Takes an Observable UserPreferences and maps and returns an Observable IUserPreferencesEntity object
     */
    override fun mapToEntity(type: Observable<UserPreferences>): Observable<IUserPreferencesEntity> {
        return type.map {
            val userPreferencesEntity = UserPreferencesEntity()
            userPreferencesEntity.id = it.id
            userPreferencesEntity.setSourceLanguageId(it.sourceLanguage.id)
            userPreferencesEntity.setTargetLanguageId(it.targetLanguage.id)
            userPreferencesEntity
        }
    }

}