package persistence.mapping

import data.User
import persistence.model.IUserEntity
import persistence.model.UserEntity

class UserMapper: Mapper<IUserEntity, User>{
    private val languageMapper = LanguageMapper()
    override fun mapToEntity(type: User): IUserEntity {
        val userEntity = UserEntity()
        userEntity.id = type.id
        userEntity.audioHash = type.audioHash
        userEntity.setAudioPath(type.audioPath)
        userEntity.sourceLanguages.addAll(type.sourceLanguages.map { languageMapper.mapToEntity(it) })
        userEntity.targetLanguages.addAll(type.targetLanguages.map { languageMapper.mapToEntity(it) })
        userEntity.preferredSourceLanguage = type.preferredSourceLanguage
        userEntity.preferredTargetLanguage = type.preferredTargetLanguage
        return userEntity
    }

    override fun mapFromEntity(type: IUserEntity): User {
        return User(
                type.id,
                type.audioHash,
                type.audioPath,
                type.sourceLanguages.map { languageMapper.mapFromEntity(it) }.toMutableList(),
                type.targetLanguages.map { languageMapper.mapFromEntity(it) }.toMutableList(),
                type.preferredSourceLanguage,
                type.preferredTargetLanguage
        )
    }

}