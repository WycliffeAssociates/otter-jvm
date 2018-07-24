package persistence

import data.Language
import data.User
import data.dao.Dao
import data.persistence.AppDatabase
import javax.inject.Inject

class AppDatabaseImpl @Inject constructor(private val userRepo: Dao<User>,
                                          private val languageRepo: Dao<Language>): AppDatabase {

    override fun getUserDao(): Dao<User> {
        return userRepo
    }

    override fun getLanguageDao(): Dao<Language> {
        return languageRepo
    }
}