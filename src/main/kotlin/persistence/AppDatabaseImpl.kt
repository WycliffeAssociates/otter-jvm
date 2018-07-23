package persistence

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

object AppDatabaseImpl: AppDatabase {
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

    private val dataStore: KotlinEntityDataStore<Persistable> = createDataStore()
    private val languageDao: Dao<Language> = LanguageRepo(dataStore)
    private val userLanguageRepo: UserLanguageRepo = UserLanguageRepo(dataStore)
    private val userDao: Dao<User> = UserRepo(dataStore, userLanguageRepo, languageDao)


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

}