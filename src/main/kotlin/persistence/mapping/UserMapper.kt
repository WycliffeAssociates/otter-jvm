package persistence.mapping

import data.User
import persistence.model.IUserEntity
import persistence.model.UserEntity

class UserMapper: Mapper<IUserEntity, User>{
    override fun mapToEntity(type: User): IUserEntity {
        val userEntity = UserEntity()
        userEntity.id = type.id
        userEntity.audioHash = type.hash
        userEntity.audioPath = type.recordedNamePath
        return userEntity

    }

    override fun mapFromEntity(type: IUserEntity): User {
        return User(
                type.id,
                type.audioHash,
                type.audioPath
        )
    }

}