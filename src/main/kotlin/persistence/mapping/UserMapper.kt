package persistence.mapping

import data.model.Language
import data.model.User
import data.dao.Dao
import data.mapping.Mapper
import persistence.model.*
import persistence.repo.UserLanguageRepo

class UserMapper(private val userLanguageRepo: UserLanguageRepo,
                 private val languageRepo: Dao<Language>): Mapper<IUserEntity, User> {
    private val userPreferencesMapper = UserPreferencesMapper(languageRepo)
    /**
     * takes a User object and maps and returns a IUserEntity
     */
    override fun mapToEntity(type: User): IUserEntity {
        val userEntity = UserEntity()
        userEntity.id = type.id
        userEntity.setAudioHash(type.audioHash)
        userEntity.setAudioPath(type.audioPath)
        userEntity.setUserPreferencesEntity(userPreferencesMapper.mapToEntity(type.userPreferences))
        return userEntity
    }

    /**
     * Takes a IUserEntity and maps and returns a User object
     */
    override fun mapFromEntity(type: IUserEntity): User {
        // queries to find all the source languages
        // kept blocking calls here because we need them to be able to return a user rather than an Observable<User>

        // Gets all rows from db's reference table by user's ID
        // so that we can figure out their source and target languages
        val userLanguages = userLanguageRepo.getByUserId(type.id).blockingFirst()

        // Filter through lists of all languages (source, then target) to see which are flagged as source,
        // then maps flagged languages to language objects
        val sourceLanguages = userLanguages.filter { it.source }
                .map { languageRepo.getById(it.languageEntityid).blockingFirst() }
        val targetLanguages = userLanguages.filter { !it.source }
                .map { languageRepo.getById(it.languageEntityid).blockingFirst() }


        // Turns preference entity into preference object
        val userPreferences = userPreferencesMapper.mapFromEntity(type.userPreferencesEntity)
        return User(
                type.id,
                type.audioHash,
                type.audioPath,
                sourceLanguages.toMutableList(),
                targetLanguages.toMutableList(),
                userPreferences
        )
    }

}