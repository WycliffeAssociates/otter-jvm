package persistance.mapping

import persistance.model.UserModel
import persistance.model.UserModelEntity

class UserMapper: Mapper<UserModel,User> {
    override fun mapToEntity(type: User): UserModel {
        val tmp = UserModelEntity()
        tmp.id = type.id
        tmp.hash = type.hash
        tmp.recordedNamePath = type.recordedNamePath
    }

    override fun mapFromEntity(type: UserModelEntity): User {
        return User(
                type.id,
                type.hash,
                type.recordedNamePath
        )
    }

}