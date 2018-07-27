package persistence.repo

import data.model.Language
import data.model.User
import data.dao.Dao
import data.dao.UserDao
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import persistence.mapping.UserMapper
import persistence.mapping.UserPreferencesMapper
import persistence.tables.daos.UserEntityDao
import persistence.tables.daos.UserPreferencesEntityDao
import persistence.tables.pojos.UserEntity
import persistence.tables.pojos.UserLanguagesEntity
import persistence.tables.pojos.UserPreferencesEntity
import tornadofx.confirm

class UserRepo(
        config: org.jooq.Configuration,
        private val userLanguageRepo: UserLanguageRepo,
        private val languageRepo : Dao<Language>
) : UserDao {
    // uses generated dao to interact with database
    private val userEntityDao = UserEntityDao(config)
    private val userPreferencesEntityDao = UserPreferencesEntityDao(config)
    private val userMapper = UserMapper(userLanguageRepo, languageRepo, userPreferencesEntityDao)
    /**
     * function to create and insert a user into the database
     * takes in a audioHash and a path to a recording to creaete
     */
    override fun insert(user: User): Observable<Int> {
        // creates observable to return generated int
        return Observable.create<Int> {
            val id: Int
            userEntityDao.insert(userMapper.mapToEntity(user))
            userPreferencesEntityDao.insert(UserPreferencesMapper(languageRepo).mapToEntity(user.userPreferences))
            // queries unique audio hash to get id of last inserted
            it.onNext(userEntityDao.fetchByAudiohash(user.audioHash).first().id)
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
        }.subscribeOn(Schedulers.io())
    }

    // considering return a new update user that contains the language for ease of use
    override fun addLanguage(user: User, language: Language, isSource: Boolean) : Completable {
        return Completable.fromAction {
            userLanguageRepo.insert(
                    UserLanguagesEntity(
                            user.id,
                            language.id,
                            if(isSource) 1 else 0
                    )
            ).blockingFirst()
        }.subscribeOn(Schedulers.io())
    }

    override fun removeLanguage(user: User, language: Language, isSource: Boolean) : Completable {
        return Completable.fromAction {
            userLanguageRepo.delete(
                    UserLanguagesEntity(
                            user.id,
                            language.id,
                            if(isSource) 1 else 0
                    )
            ).blockingAwait()
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

    override fun setLanguagePreference(user: User, language: Language, isSource: Boolean) : Completable {
        return Completable.fromAction {
            val userPreferences = userPreferencesEntityDao.fetchByUserfk(user.id).first()
            // updates either the preferred source or target language
            if (isSource) {
                userPreferences.sourcelanguagefk = language.id
            } else {
                userPreferences.targetlanguagefk = language.id
            }
            userPreferencesEntityDao.update(userPreferences)
        }.subscribeOn(Schedulers.io())
    }

    /**
     * This function should be unnecessary because we have add and remove function
     * this then enforces clients to add and remove languages through the database
     * keeping just in case we want this
     */
    /*
    private fun updateUserLanguageReferences(user: User, userId: Int) {
        // inserts source and target languages into user language relationship table
        val newSourceUserLanguages = user.sourceLanguages.map {
            val userSourceLanguage = UserLanguage()
            userSourceLanguage.setLanguageEntityid(it.id)
            userSourceLanguage.setUserEntityid(userId)
            userSourceLanguage.setSource(true)
            userSourceLanguage
        }
        val newTargetUserLanguages = user.targetLanguages.map {
            val userTargetLanguage = UserLanguage()
            userTargetLanguage.setLanguageEntityid(it.id)
            userTargetLanguage.setUserEntityid(userId)
            userTargetLanguage.setSource(false)
            userTargetLanguage
        }
        val newUserLanguages = newTargetUserLanguages.union(newSourceUserLanguages)
        // blocking first might be okay since this entire function is used in a observable doOnNext
        // may be able to be refactored to avoid this
        val userLanguages = userLanguageRepo.getByUserId(userId).blockingFirst()

        newUserLanguages.forEach { newUserLanguage ->
            // only insert the userlanguage into the junction table if the row doesn't already exist
            if (userLanguages.filter {
                        it.languageEntityid == newUserLanguage.languageEntityid &&
                        it.source == newUserLanguage.source
                    }.isEmpty()) {
                // inserting language reference
                userLanguageRepo.insert(newUserLanguage).blockingFirst()
            }
        }

        userLanguages.forEach { userLanguage ->
            if (newUserLanguages.filter {
                        it.languageEntityid == userLanguage.languageEntityid &&
                        it.source == userLanguage.source
                    }.isEmpty()) {
                userLanguageRepo.delete(userLanguage).blockingAwait()
            }
        }
    }
    */
}