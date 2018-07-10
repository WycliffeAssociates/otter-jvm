package app.backEnd

import app.requery.User
import app.requery.UserEntity
import io.requery.kotlin.eq

class UserHandler: DBHandler(){

    /**
     * function to create and insert a user into the database
     * takes in a hash and a path to a recording to creaete
     */
    fun createUser(hash:String, recordedNamePath: String): User {
        // attempts to find if user already exists
        val tmp = UserEntity()
        tmp.hash = hash
        tmp.recordedNamePath = recordedNamePath
        // returns created user
        return dataStore.insert(tmp)
    }

    /**
     * gets all the users currently stored in db
     */
    fun getUsers(): List<User>{
        return dataStore {
            val result = dataStore.select(User::class)
            result.get().toList()
        }
    }

    /**
     * given a hash gets the user
     */
    fun getUser(hash: String): User{
        return dataStore {
            val result = dataStore.select(User::class).where(User::hash eq hash)
            result.get().first()
        }
    }

    /**
     * given a hash deletrs the user
     */
    fun deleteUser(hash: String){
        dataStore.delete(User::class).where(User::hash eq hash).get().value()
    }
}