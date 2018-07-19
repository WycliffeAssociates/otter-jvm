package persistence.mapping

import data.Language
import data.User
import data.dao.Dao
import io.requery.Persistable
import io.requery.kotlin.eq
import io.requery.sql.KotlinEntityDataStore
import persistence.model.*
import javax.inject.Inject

class UserMapper @Inject constructor(private val dataStore: KotlinEntityDataStore<Persistable>, private val languageRepo: Dao<Language>, private val userPreferencesMapper: UserPreferencesMapper): Mapper<IUserEntity, User>{
    /**
     * takes a User object and maps and returns a IUserEntity
     */
    override fun mapToEntity(type: User): IUserEntity {
        val userEntity = UserEntity()
        userEntity.id = type.id
        userEntity.setAudioHash(type.audioHash)
        userEntity.setAudioPath(type.audioPath)
        return userEntity
    }

    /**
     * Takes a IUserEntity and maps and returns a User object
     */
    override fun mapFromEntity(type: IUserEntity): User {
        // queries to find all the source languages
        val sourceLanguages = dataStore
                .select(IUserLanguage::class).where((IUserLanguage::userEntityid eq type.id) and (IUserLanguage::source eq true)).get().toList()
                .map { languageRepo.getById(it.languageEntityid).blockingFirst() }.toMutableList()
        // queries to find target languages
        val targetLanguages = dataStore
                .select(IUserLanguage::class)
                .where((IUserLanguage::userEntityid eq type.id) and (IUserLanguage::source eq false)).get().toList()
                .map { languageRepo.getById(it.languageEntityid).blockingFirst() }.toMutableList()
        val userPreferences = userPreferencesMapper.mapFromEntity(type.userPreferencesEntity)//dataStore.select(IUserPreferencesEntity::class).
        return User(
                type.id,
                type.audioHash,
                type.audioPath,
                sourceLanguages,
                targetLanguages,
                userPreferences
        )
    }

}