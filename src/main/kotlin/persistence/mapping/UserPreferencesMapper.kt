package persistence.mapping

import data.DayNight
import data.Language
import data.UserPreferences
import data.dao.Dao
import io.requery.Persistable
import io.requery.kotlin.eq
import io.requery.sql.KotlinEntityDataStore
import persistence.model.ILanguageEntity
import persistence.model.IUserEntity
import persistence.model.IUserPreferencesEntity
import persistence.model.UserPreferencesEntity
import javax.inject.Inject

class UserPreferencesMapper @Inject constructor(private val languageRepo: Dao<Language>):
        Mapper<IUserPreferencesEntity, UserPreferences> {

    override fun mapFromEntity(type: IUserPreferencesEntity): UserPreferences {
        // gets from database and maps preferred source and target language
        val preferredSourceLanguage = languageRepo.getById(type.preferredSourceLanguageId).blockingFirst()
        val preferredTargetLanguage = languageRepo.getById(type.preferredTargetLanguageId).blockingFirst()

        return UserPreferences(
                id = type.id,
                preferredTargetLanguage = preferredTargetLanguage,
                preferredSourceLanguage = preferredSourceLanguage,
                dayNightMode = enumValues<DayNight>()[type.dayNightMode],
                uiLanguagePreferences = type.uiLanguagePreference
        )
    }

    override fun mapToEntity(type: UserPreferences): IUserPreferencesEntity {
        val userPreferencesEntity = UserPreferencesEntity()
        userPreferencesEntity.preferredSourceLanguageId = type.preferredSourceLanguage.id
        userPreferencesEntity.preferredTargetLanguageId = type.preferredTargetLanguage.id
        userPreferencesEntity.dayNightMode = type.dayNightMode.ordinal
        userPreferencesEntity.uiLanguagePreference = type.uiLanguagePreferences
        return  userPreferencesEntity
    }

}