package persistence

import data.Language
import data.User
import data.dao.Dao
import data.persistence.TrDatabase
import javax.inject.Inject

class TrDatabaseImpl @Inject constructor(private val userRepo: Dao<User>,
                                         private val languageRepo: Dao<Language>): TrDatabase {

    override fun getUserDao(): Dao<User> {
        return userRepo
    }

    override fun getLanguageDao(): Dao<Language> {
        return languageRepo
    }
}