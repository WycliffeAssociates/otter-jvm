package persistence.repo

import data.model.Language
import data.model.User
import data.dao.Dao
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.requery.Persistable
import io.requery.kotlin.eq
import io.requery.kotlin.set
import io.requery.sql.KotlinEntityDataStore
import persistence.mapping.UserMapper
import persistence.model.IUserEntity
import persistence.model.IUserLanguage
import persistence.model.UserLanguage
import javax.inject.Inject

class UserRepo(private val dataStore: KotlinEntityDataStore<Persistable>,
               private val userLanguageRepo: UserLanguageRepo, languageDao : Dao<Language>): Dao<User> {
    private val userMapper = UserMapper(userLanguageRepo, languageDao)
    /**
     * function to create and insert a user into the database
     * takes in a audioHash and a path to a recording to creaete
     */
    override fun insert(user: User): Observable<Int> {
        // creates observable to return generated int
        return Observable.create<Int> {
            it.onNext(dataStore.insert(userMapper.mapToEntity(user)).id)
        }.doOnNext {
            updateUserLanguageReferences(user, it)
        }.subscribeOn(Schedulers.io())
    }

    /**
     * gets user by Id
     */
    override fun getById(id:Int): Observable<User>{
        return Observable.create<IUserEntity> {
            it.onNext(dataStore.select(IUserEntity::class)
                    .where(IUserEntity::id eq id)
                    .get().first())
        }.map {
            userMapper.mapFromEntity(it)
        }.subscribeOn(Schedulers.io())
    }

    /**
     * given a audioHash gets the user
     */
    fun getByHash(hash: String): Observable<User> {
        return Observable.create<IUserEntity> {
            dataStore.select(IUserEntity::class)
                    .where(IUserEntity::audioHash eq hash)
                    .get().first()
        }.map {
            userMapper.mapFromEntity(it)
        }.subscribeOn(Schedulers.io())
    }

    /**
     * gets all the users currently stored in db
     */
    override fun getAll(): Observable<List<User>>{
        return Observable.create<List<IUserEntity>> {
            it.onNext(dataStore.select(IUserEntity::class)
                    .get().toList())
        }.map {
            it.map { userMapper.mapFromEntity(it) }
        }.subscribeOn(Schedulers.io())

    }

    override fun update(user: User): Completable {
        return Completable.fromAction {
            val userEntity = userMapper.mapToEntity(user)
            dataStore.update(userEntity)
            dataStore.update(userEntity.userPreferencesEntity)
            updateUserLanguageReferences(user, user.id)
        }.subscribeOn(Schedulers.io())
    }

    /**
     * deletes user by id
     */
    override fun delete(user: User): Completable{
        return Completable.fromAction {
            val userEntity = userMapper.mapToEntity(user)
            dataStore.delete(userEntity)
            dataStore.delete(IUserLanguage::class).where(IUserLanguage::userEntityid eq user.id).get().value()
        }.subscribeOn(Schedulers.io())
    }

    private fun updateUserLanguageReferences(user: User, userId: Int) {
        // inserts source and target languages into user language relationship table
        val newSourceUserLanguages = user.sourceLanguages.map {
            val tmp = UserLanguage()
            tmp.setLanguageEntityid(it.id)
            tmp.setUserEntityid(userId)
            tmp.setSource(true)
            tmp
        }
        val newTargetUserLanguages = user.targetLanguages.map {
            val tmp = UserLanguage()
            tmp.setLanguageEntityid(it.id)
            tmp.setUserEntityid(userId)
            tmp.setSource(false)
            tmp
        }
        val newUserLanguages = newTargetUserLanguages.union(newSourceUserLanguages)
        // blocking first might be okay since this entire function is used in a observable doOnNext
        // may be able to be refactored to avoid this
        val userLanguages = userLanguageRepo.getByUserId(userId).blockingFirst()

        newUserLanguages.forEach { newUserLanguage ->
            // only insert the userlanguage into the junction table if the row doesn't already exist
            if(userLanguages.filter {
                        it.languageEntityid == newUserLanguage.languageEntityid && it.source == newUserLanguage.source
                    }.isEmpty()) {
                // inserting language reference
                userLanguageRepo.insert(newUserLanguage).blockingFirst()
            }
        }

        userLanguages.forEach { userLanguage ->
            if (newUserLanguages.filter {
                        it.languageEntityid == userLanguage.languageEntityid && it.source == userLanguage.source
                    }.isEmpty()) {
                userLanguageRepo.delete(userLanguage).blockingAwait()
            }
        }
    }
}