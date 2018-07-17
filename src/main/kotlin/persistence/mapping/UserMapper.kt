package persistence.mapping

import data.Language
import data.User
import io.requery.Persistable
import io.requery.kotlin.eq
import io.requery.sql.KotlinEntityDataStore
import persistence.model.ILanguageEntity
import persistence.model.IUserEntity
import persistence.model.IUserLanguage
import persistence.model.UserEntity
import persistence.repo.LanguageRepo

class UserMapper(private val dataStore: KotlinEntityDataStore<Persistable>): Mapper<IUserEntity, User>{
    private val languageMapper = LanguageMapper()

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
                .select(ILanguageEntity::class)
                .join(IUserLanguage::class).on(ILanguageEntity::id eq IUserLanguage::languageEntityid)
                .where((IUserLanguage::userEntityid eq type.id) and (IUserLanguage::source eq true)).get()
                .map { languageMapper.mapFromEntity(it) }.toMutableList()
        // queries to find target languages
        val targetLanguages = dataStore
                .select(ILanguageEntity::class)
                .join(IUserLanguage::class).on(ILanguageEntity::id eq IUserLanguage::languageEntityid)
                .where((IUserLanguage::userEntityid eq type.id) and (IUserLanguage::source eq false)).get()
                .map { languageMapper.mapFromEntity(it) }.toMutableList()
        return User(
                type.id,
                type.audioHash,
                type.audioPath,
                sourceLanguages,
                targetLanguages
        )
    }

}