package persistence

import data.Language
import data.User
import data.dao.Dao
import data.persistence.AppDatabase
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

class AppDatabaseImpl: AppDatabase {

    private val dataStore: KotlinEntityDataStore<Persistable> = createDataStore()
    private var languageDao: Dao<Language> = LanguageRepo(dataStore)
    private var userDao: Dao<User> = UserRepo(dataStore, languageDao)


    private fun createDataStore() : KotlinEntityDataStore<Persistable> {
        Class.forName("org.sqlite.JDBC")

        val sqLiteDataSource = SQLiteDataSource()
        sqLiteDataSource.url = "jdbc:sqlite:content.db"

        // creates tables that do not already exist
        SchemaModifier(sqLiteDataSource, Models.DEFAULT).createTables(TableCreationMode.CREATE_NOT_EXISTS)

        // sets up data store
        val config = KotlinConfiguration(dataSource = sqLiteDataSource, model = Models.DEFAULT)
        return KotlinEntityDataStore<Persistable>(config)
    }

    override fun getUserDao(): Dao<User> {
        return userDao
    }

    override fun getLanguageDao(): Dao<Language> {
        return languageDao
    }

    companion object {
        private var instance : AppDatabase = AppDatabaseImpl() //? = null

        fun getInstance() : AppDatabase {
            /*
            if (instance == null) {
                synchronized(AppDatabaseImpl::class) {
                    instance = AppDatabaseImpl()
                }

            }
            */
            return instance
        }
    }

}