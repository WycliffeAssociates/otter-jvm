package persistence.repo

import data.model.Language
import data.model.User
import data.dao.Dao
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import persistence.mapping.UserMapper
import persistence.mapping.UserPreferencesMapper
import persistence.tables.daos.UserEntityDao
import persistence.tables.daos.UserLanguagesEntityDao
import persistence.tables.daos.UserPreferencesEntityDao
import persistence.tables.pojos.UserEntity
import persistence.tables.pojos.UserLanguagesEntity

class UserRepo(
        config: org.jooq.Configuration,
        private val languageRepo : Dao<Language>
) : Dao<User > {
    // uses generated dao to interact with database
    private val userEntityDao = UserEntityDao(config)
    private val userPreferencesEntityDao = UserPreferencesEntityDao(config)
    private val userMapper = UserMapper(UserLanguageRepo(config), languageRepo, userPreferencesEntityDao)
    private val userPreferencesMapper = UserPreferencesMapper(languageRepo)
    private val userLanguageEntityDao = UserLanguagesEntityDao(config)
    /**
     * function to create and insert a user into the database
     * takes in a audioHash and a path to a recording to creaete
     */
    override fun insert(user: User): Observable<Int> {
        // creates observable to return generated int
        return Observable.create<Int> {
            val id: Int
            userEntityDao.insert(userMapper.mapToEntity(user))
            // queries unique audio hash to get id of last inserted
            it.onNext(userEntityDao.fetchByAudiohash(user.audioHash).first().id)
        }.doOnNext {
            updateUserLanguageReferences(user, it)
            user.userPreferences.id = it
            userPreferencesEntityDao.insert(userPreferencesMapper.mapToEntity(user.userPreferences))
        }.subscribeOn(Schedulers.io())
    }

    /**
     * gets user by Id
     */
    override fun getById(id:Int): Observable<User> {
        return Observable.create<UserEntity> {
            it.onNext(
                    userEntityDao.fetchById(id).first()
            )
        }.map {
            userMapper.mapFromEntity(it)
        }.subscribeOn(Schedulers.io())
    }

    /**
     * gets all the users currently stored in db
     */
    override fun getAll(): Observable<List<User>> {
        return Observable.create<List<UserEntity>> {
            it.onNext(
                    userEntityDao.findAll()
            )
        }.map {
            it.map { userMapper.mapFromEntity(it) }
        }.subscribeOn(Schedulers.io())

    }

    override fun update(user: User): Completable {
        return Completable.fromAction {
            userEntityDao.update(userMapper.mapToEntity(user))
            updateUserLanguageReferences(user, user.id)
            userPreferencesEntityDao.update(userPreferencesMapper.mapToEntity(user.userPreferences))
        }.subscribeOn(Schedulers.io())
    }

    /**
     * deletes user by id
     */
    override fun delete(user: User): Completable {
        return Completable.fromAction {
            userEntityDao.delete(userMapper.mapToEntity(user))
        }.subscribeOn(Schedulers.io())
    }

    /**
     * This function should be unnecessary because we have add and remove function
     * this then enforces clients to add and remove languages through the database
     * keeping just in case we want this
     */

    private fun updateUserLanguageReferences(user: User, userId: Int) {
        // inserts source and target languages into user language relationship table
        val newSourceUserLanguages = user.sourceLanguages.map {
            UserLanguagesEntity(
                    userId,
                    it.id,
                    1
            )
        }
        val newTargetUserLanguages = user.targetLanguages.map {
            UserLanguagesEntity(
                    userId,
                    it.id,
                    0
            )

        }
        val newUserLanguages = newTargetUserLanguages.union(newSourceUserLanguages)
        // blocking first might be okay since this entire function is used in a observable doOnNext
        // may be able to be refactored to avoid this
        val userLanguages = userLanguageEntityDao.fetchByUserfk(userId)
        newUserLanguages.forEach { newUserLanguage ->
            // only insert the userlanguage into the junction table if the row doesn't already exist
            if (userLanguages.filter {
                        it.languagefk == newUserLanguage.languagefk&&
                                it.issource == newUserLanguage.issource
                    }.isEmpty()) {
                // inserting language reference
                userLanguageEntityDao.insert(newUserLanguage)
            }
        }

        userLanguages.forEach { userLanguage ->
            if (newUserLanguages.filter {
                        it.languagefk == userLanguage.languagefk &&
                                it.issource == userLanguage.issource
                    }.isEmpty()) {
                userLanguageEntityDao.delete(userLanguage)
            }
        }
    }

}