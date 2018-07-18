package persistence.repo

import data.Language
import data.User
import data.dao.Dao
import data.persistence.TrDatabase
import io.requery.Persistable
import io.requery.sql.KotlinConfiguration
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.SchemaModifier
import io.requery.sql.TableCreationMode
import org.sqlite.SQLiteDataSource
import persistence.model.Models
import persistence.repo.LanguageRepo
import persistence.repo.UserRepo
import javax.inject.Inject

class TrDatabaseImpl @Inject constructor(private val userDao : Dao<User>, private val languageDao : Dao<Language>): TrDatabase {

    override fun getUserDao(): Dao<User> {
        return userDao
    }

    override fun geLanguageDao(): Dao<Language> {
        return languageDao
    }

}