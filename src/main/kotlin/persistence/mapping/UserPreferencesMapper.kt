package persistence.mapping

import data.model.Language
import data.model.UserPreferences
import data.dao.Dao
import data.mapping.Mapper
import persistence.model.IUserPreferencesEntity
import persistence.model.UserPreferencesEntity

class UserPreferencesMapper(private val languageRepo: Dao<Language>):
        Mapper<IUserPreferencesEntity, UserPreferences> {

    override fun mapFromEntity(type: IUserPreferencesEntity): UserPreferences {
        // gets from database and maps preferred source and target language
        val preferredSourceLanguage = languageRepo.getById(type.sourceLanguageId).blockingFirst()
        val preferredTargetLanguage = languageRepo.getById(type.targetLanguageId).blockingFirst()

        return UserPreferences(
                type.id,
                preferredSourceLanguage,
                preferredTargetLanguage
        )
    }

    override fun mapToEntity(type: UserPreferences): IUserPreferencesEntity {
        val userPreferencesEntity = UserPreferencesEntity()
        userPreferencesEntity.id = type.id
        userPreferencesEntity.sourceLanguageId = type.sourceLanguage.id
        userPreferencesEntity.targetLanguageId = type.targetLanguage.id
        return  userPreferencesEntity
    }

}