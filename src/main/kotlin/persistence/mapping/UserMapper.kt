package persistence.mapping

import persistence.model.UserModel
import persistence.model.UserModelEntity

class UserMapper: Mapper<UserModel,User> {
    override fun mapToEntity(type: User): UserModel {
        val tmp = UserModelEntity()
        tmp.id = type.id
        tmp.hash = type.hash
        tmp.recordedNamePath = type.recordedNamePath
        return tmp
    }

    override fun mapFromEntity(type: UserModelEntity): User {
        return User(
                type.id,
                type.hash,
                type.recordedNamePath
        )
    }

}