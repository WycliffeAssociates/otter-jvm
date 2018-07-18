package persistence.repo

import data.User
import data.dao.Dao
import io.reactivex.Completable
import io.reactivex.Observable
import io.requery.Persistable
import io.requery.kotlin.eq
import io.requery.kotlin.set
import io.requery.sql.KotlinEntityDataStore
import persistence.mapping.UserMapper
import persistence.model.IUserEntity
import persistence.model.IUserLanguage
import persistence.model.UserLanguage
import javax.inject.Inject

//todo implement DAO
class UserRepo @Inject constructor(val dataStore: KotlinEntityDataStore<Persistable>): Dao<User> {

    private val userMapper = UserMapper(dataStore)
    /**
     * function to create and insert a user into the database
     * takes in a audioHash and a path to a recording to creaete
     */
    override fun insert(user: User): Observable<Int> {
        // creates observable to return generated int
        val ret = Observable.just(dataStore.insert(userMapper.mapToEntity(user)).id).doOnNext { userId ->
           updateUserLanguageReferences(user,userId)
        }
        return ret
    }

    /**
     * gets user by Id
     */
    override fun getById(id:Int): Observable<User>{
        val tmp = dataStore {
            val result = dataStore.select(IUserEntity::class).where(IUserEntity::id eq id)
            result.get().first()
        }
        return Observable.just(userMapper.mapFromEntity(tmp))
    }

    /**
     * given a audioHash gets the user
     */
    fun getByHash(hash: String): Observable<User> {
        val tmp = dataStore {
            val result = dataStore.select(IUserEntity::class).where(IUserEntity::audioHash eq hash)
            result.get().first()
        }
        return Observable.just(userMapper.mapFromEntity(tmp))
    }

    /**
     * gets all the users currently stored in db
     */
    override fun getAll(): Observable<List<User>>{
        val tmp = dataStore {
            val result = dataStore.select(IUserEntity::class)
            result.get().asIterable()
        }
        return Observable.just(tmp.map { userMapper.mapFromEntity(it) })

    }

    //todo fix
    override fun update(user: User): Completable{
        return Completable.fromAction{
            dataStore.update(IUserEntity::class)
                    .set(IUserEntity::audioHash,user.audioHash)
                    .where(IUserEntity::id eq  user)
        }.doOnComplete{updateUserLanguageReferences(user,user.id)}
    }

    /**
     * deletes user by id
     */
    override fun delete(user: User): Completable{
        return Completable.fromAction{
            dataStore.delete(IUserLanguage::class).where(IUserLanguage::userEntityid eq user.id).get().value()
            dataStore.delete(IUserEntity::class).where(IUserEntity::id eq user.id).get().value()
        }
    }

    private fun updateUserLanguageReferences(user: User, userId: Int){
        // inserts source and target languages into user language relationship table
        val newUserLanguages = user.sourceLanguages.union(user.targetLanguages).map {
            val tmp = UserLanguage()
            tmp.setLanguageEntityid(it.id)
            tmp.setUserEntityid(userId)
            tmp.setSource(user.sourceLanguages.contains(it))
            tmp
        }
        val userLanguages = dataStore.select(IUserLanguage::class).where(IUserLanguage::userEntityid eq userId).get().toList()

        newUserLanguages.forEach { newUserLanguage ->
            // only insert the userlanguage into the junction table if the row doesn't already exist
            if(userLanguages.filter {
                        it.languageEntityid == newUserLanguage.languageEntityid && it.source == newUserLanguage.source
                    }.isEmpty()) {
                dataStore.insert(newUserLanguage)
            }
        }

        userLanguages.forEach { userLanguage ->
            if (newUserLanguages.filter {
                        it.languageEntityid == userLanguage.languageEntityid && it.source == userLanguage.source
                    }.isEmpty()) {
                dataStore.delete(userLanguage)
            }
        }
    }
}