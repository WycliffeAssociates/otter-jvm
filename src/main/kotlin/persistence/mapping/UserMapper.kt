package persistence.mapping

import data.model.Language
import data.model.User
import data.dao.Dao
import data.mapping.Mapper
import persistence.repo.UserLanguageRepo
import persistence.tables.daos.UserPreferencesEntityDao
import persistence.tables.pojos.UserEntity

class UserMapper(
        private val userLanguageRepo: UserLanguageRepo,
        private val languageRepo: Dao<Language>,
        private val userPreferencesEntityDao: UserPreferencesEntityDao
): Mapper<UserEntity, User> {

    private val userPreferencesMapper = UserPreferencesMapper(languageRepo)
    /**
     * takes a User object and maps and returns a persistence.tables.pojos.UserEntity
     */
    override fun mapToEntity(type: User): UserEntity {
        return UserEntity(
                type.id,
                type.audioHash,
                type.audioPath,
                type.imagePath
        )
    }

    /**
     * Takes a persistence.tables.pojos.UserEntity and maps and returns a User object
     */
    override fun mapFromEntity(type: UserEntity): User {
        // queries to find all the source languages
        // kept blocking calls here because we need them to be able to return a user rather than an Observable<User>
        val userLanguages = userLanguageRepo.getByUserId(type.id).blockingFirst()

        // finds all the sources and target languages associated with the ids in userLanguages
        val sourceLanguages = userLanguages
                .filter { it.issource == 1 }
                .map { languageRepo.getById(it.languagefk).blockingFirst() }
        val targetLanguages = userLanguages
                .filter { it.issource == 0 }
                .map { languageRepo.getById(it.languagefk).blockingFirst() }

        val userPreferences = userPreferencesMapper.mapFromEntity(
                userPreferencesEntityDao
                        .fetchOneByUserfk(type.id)
        )
        return User(
                id = type.id,
                audioHash = type.audiohash,
                audioPath = type.audiopath,
                imagePath = type.imgpath,
                sourceLanguages = sourceLanguages.toMutableList(),
                targetLanguages = targetLanguages.toMutableList(),
                userPreferences = userPreferences
        )
    }

}