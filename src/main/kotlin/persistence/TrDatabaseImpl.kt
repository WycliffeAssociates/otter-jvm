package persistence

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
import persistence.mapping.LanguageMapper
import persistence.mapping.UserMapper
import persistence.model.Models
import persistence.repo.LanguageRepo
import persistence.repo.UserRepo

class TrDatabaseImpl: TrDatabase {
    private val dataStore: KotlinEntityDataStore<Persistable>
    private val userRepo: Dao<User>
    private val languageRepo: Dao<Language>

    init {
        Class.forName("org.sqlite.JDBC")

        val sqLiteDataSource = SQLiteDataSource()
        sqLiteDataSource.url = "jdbc:sqlite:tr.db"

        // creates tables that do not already exist
        SchemaModifier(sqLiteDataSource, Models.DEFAULT).createTables(TableCreationMode.CREATE_NOT_EXISTS)

        // sets up data store
        val config = KotlinConfiguration(dataSource = sqLiteDataSource, model = Models.DEFAULT)
        dataStore = KotlinEntityDataStore(config)

        languageRepo = LanguageRepo(dataStore)
        userRepo = UserRepo(dataStore, languageRepo)

    }

    override fun getUserDao(): Dao<User> {
        return userRepo
    }

    override fun getLanguageDao(): Dao<Language> {
        return languageRepo
    }


    // function to close the data store
    fun closeStore(){
        dataStore.close()
    }
}