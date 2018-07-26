package persistence.mapping

import data.model.Language
import data.model.UserPreferences
import data.dao.Dao
import data.mapping.Mapper
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import persistence.model.IUserPreferencesEntity
import persistence.model.UserPreferencesEntity

class UserPreferencesMapper(private val languageRepo: Dao<Language>):
        Mapper<IUserPreferencesEntity, Observable<UserPreferences>> {

    override fun mapFromEntity(type: IUserPreferencesEntity): Observable<UserPreferences> {
        // gets from database and maps preferred source and target language
        val preferredSourceLanguage = languageRepo.getById(type.sourceLanguageId)
        val preferredTargetLanguage = languageRepo.getById(type.targetLanguageId)
        return Observable.zip(preferredSourceLanguage, preferredTargetLanguage,
                BiFunction<Language, Language, UserPreferences>{a, b -> UserPreferences(type.id, a, b)})
    }

    override fun mapToEntity(type: Observable<UserPreferences>): IUserPreferencesEntity {
        val userPreferencesEntity = UserPreferencesEntity()
        type.subscribe({it -> userPreferencesEntity.id = it.id;
                              userPreferencesEntity.setSourceLanguageId(it.sourceLanguage.id);
                              userPreferencesEntity.setTargetLanguageId(it.targetLanguage.id)
        })
        return userPreferencesEntity
    }

}