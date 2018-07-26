package persistence

import app.filesystem.DirectoryProvider
import data.dao.Dao
import data.model.*
import data.persistence.AppDatabase
import io.requery.Persistable
import io.requery.sql.KotlinConfiguration
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.SchemaModifier
import io.requery.sql.TableCreationMode
import org.sqlite.SQLiteDataSource
import persistence.model.Models
import persistence.repo.LanguageRepo
import persistence.repo.UserLanguageRepo
import persistence.repo.UserRepo
import java.io.File
import java.nio.file.FileSystems

// Db object; repository of Data Access Objects (daos)
// The place where we connect to the SQLite db
object AppDatabaseImpl: AppDatabase {

    //We will implement other daos later
    override fun getAnthologyDao(): Dao<Anthology> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBookDao(): Dao<Book> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getChapterDao(): Dao<Chapter> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getChunkDao(): Dao<Chunk> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getProjectDao(): Dao<Project> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTakesDao(): Dao<Take> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    // requery database: the object that requery provides to insert, remove, update, etc.
    private val dataStore: KotlinEntityDataStore<Persistable> = createDataStore()

    // Instances of the daos so we can return them via the getters; will add more later
    private val languageDao: Dao<Language> = LanguageRepo(dataStore)
    private val userLanguageRepo: UserLanguageRepo = UserLanguageRepo(dataStore)
    private val userDao: Dao<User> = UserRepo(dataStore, userLanguageRepo, languageDao)


    private fun createDataStore() : KotlinEntityDataStore<Persistable> {
        // Initializes the database drivers, java's SQL interpreters
        // Lets us read sqlite
        Class.forName("org.sqlite.JDBC")

        // Sets up location of database file we want to connect to
        // Tells us where to get data from (path)
        val sqLiteDataSource = SQLiteDataSource()
        sqLiteDataSource.url = "jdbc:sqlite:" +
                DirectoryProvider("8woc2018").getAppDataDirectory() +
                FileSystems.getDefault().separator +
                "content.sqlite"

        // Creates tables inside the database we just connected to if they do not already exist
        SchemaModifier(sqLiteDataSource, Models.DEFAULT).createTables(TableCreationMode.CREATE_NOT_EXISTS)

        // Sets up data store
        // Tells requery how we want it to configure database connection for us
        val config = KotlinConfiguration(dataSource = sqLiteDataSource, model = Models.DEFAULT)
        return KotlinEntityDataStore<Persistable>(config)
    }

    override fun getUserDao(): Dao<User> {
        return userDao
    }

    override fun getLanguageDao(): Dao<Language> {
        return languageDao
    }

}