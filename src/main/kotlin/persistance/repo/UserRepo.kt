package persistance.repo

import app.requery.UserModelEntity
import io.requery.Persistable
import io.requery.kotlin.eq
import io.requery.sql.KotlinEntityDataStore
import persistance.model.UserModel

class UserRepo(dataStore: KotlinEntityDataStore<Persistable>){
    private val dataStore = dataStore
    /**
     * function to create and insert a user into the database
     * takes in a hash and a path to a recording to creaete
     */
    fun insert(hash:String, recordedNamePath: String): UserModel {
        // attempts to find if user already exists
        val tmp = UserModelEntity()
        tmp.hash = hash
        tmp.recordedNamePath = recordedNamePath
        // returns created user
        return dataStore.insert(tmp)
    }

    /**
     * gets user by Id
     */
    fun getById(id:Int){
        return dataStore {
            val result = dataStore.select(UserModel::class).where(UserModel::id eq id)
            result.get().first()
        }
    }


    /**
     * given a hash gets the user
     */
    fun getByHash(hash: String): UserModel {
        return dataStore {
            val result = dataStore.select(UserModel::class).where(UserModel::hash eq hash)
            result.get().first()
        }
    }

    /**
     * gets all the users currently stored in db
     */
    fun getAll(): List<UserModel>{
        return dataStore {
            val result = dataStore.select(UserModel::class)
            result.get().toList()
        }
    }


    fun updateUser(hash: String, newPath: String){
        return dataStore {
            //todo figure out
        }
    }

    /**
     * deletes user by id
     */
    fun delete(user: UserModel){
        dataStore.delete(UserModel::class).where(UserModel::id eq user.id).get().value()
    }

}