package persistence.mapping

import data.model.Language
import data.model.UserPreferences
import data.dao.Dao
import data.mapping.Mapper
import persistence.tables.pojos.UserPreferencesEntity

class UserPreferencesMapper(private val languageRepo: Dao<Language>):
        Mapper<UserPreferencesEntity, UserPreferences> {

    override fun mapFromEntity(type: UserPreferencesEntity): UserPreferences {
        // gets from database and maps preferred source and target language
        val preferredSourceLanguage = languageRepo.getById(type.sourcelanguagefk)
                                                  .blockingFirst()
        val preferredTargetLanguage = languageRepo.getById(type.targetlanguagefk)
                                                  .blockingFirst()

        return UserPreferences(
                type.userfk,
                preferredSourceLanguage,
                preferredTargetLanguage
        )
    }

    override fun mapToEntity(type: UserPreferences): UserPreferencesEntity {
        return UserPreferencesEntity(
                type.id,
                type.sourceLanguage.id,
                type.targetLanguage.id
        )
    }

}