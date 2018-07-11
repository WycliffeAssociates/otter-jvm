package persistence.mapping

import data.User
import persistence.model.IUserEntity
import persistence.model.UserEntity

class UserMapper: Mapper<IUserEntity, User>{
    override fun mapToEntity(type: User): IUserEntity {
        val tmp = UserEntity()
        tmp.id = type.id
        tmp.hash = type.hash
        tmp.recordedNamePath = type.recordedNamePath
        return tmp

    }

    override fun mapFromEntity(type: IUserEntity): User {
        return User(
                type.id,
                type.hash,
                type.recordedNamePath
        )
    }

}