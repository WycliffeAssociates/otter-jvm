package persistance.repo

import io.reactivex.Completable
import io.reactivex.Observable
import io.requery.Persistable
import io.requery.kotlin.eq
import io.requery.sql.KotlinEntityDataStore
import persistance.mapping.UserMapper
import persistance.model.UserModel
import persistance.model.UserModelEntity

//todo implement DAO
class UserRepo(dataStore: KotlinEntityDataStore<Persistable>): Dao<User>{
    private val dataStore = dataStore
    private val userMapper = UserMapper()
    /**
     * function to create and insert a user into the database
     * takes in a hash and a path to a recording to creaete
     */
    override fun insert(user: User): Observable<Int> {
        // returns created user
        return Observable.just(dataStore.insert(userMapper.mapToEntity(user)).id)
    }

    /**
     * gets user by Id
     */
    override fun getById(id:Int): Observable<User>{
        val tmp = dataStore {
            val result = dataStore.select(UserModel::class).where(UserModel::id eq id)
            result.get().first()
        }
        return Observable.just(userMapper.mapFromEntity(tmp))
    }

    /**
     * given a hash gets the user
     */
    fun getByHash(hash: String): Observable<User> {
        val tmp = dataStore {
            val result = dataStore.select(UserModel::class).where(UserModel::hash eq hash)
            result.get().first()
        }
        return Observable.just(userMapper.mapFromEntity(tmp))
    }

    /**
     * gets all the users currently stored in db
     */
    override fun getAll(): Observable<List<User>>{
        val tmp = dataStore {
            val result = dataStore.select(UserModel::class)
            result.get().asIterable()
        }
        return Observable.fromIterable(tmp.map { User(it.id, it.hash, it.recordedNamePath) })

    }

    fun updateUser(hash: String, newPath: String): Completable{
        //todo figure out
    }

    /**
     * deletes user by id
     */
    fun delete(user: User): Completable{
        return Completable.fromAction{
            dataStore.delete(UserModel::class).where(UserModel::id eq user.id).get().value()
        }
    }
}