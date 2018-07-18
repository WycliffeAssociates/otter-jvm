package persistence.repo

import dagger.Module
import dagger.Provides
import data.Language
import data.User
import data.dao.Dao
import io.requery.Persistable
import io.requery.sql.KotlinConfiguration
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.SchemaModifier
import io.requery.sql.TableCreationMode
import org.sqlite.SQLiteDataSource
import persistence.model.Models
import javax.inject.Singleton

@Module
class Module {
    @Provides
    @Singleton
    fun providesDataStore() : KotlinEntityDataStore<Persistable>{
        Class.forName("org.sqlite.JDBC")

        val sqLiteDataSource = SQLiteDataSource()
        sqLiteDataSource.url = "jdbc:sqlite:tr.db"

        // creates tables that do not already exist
        SchemaModifier(sqLiteDataSource, Models.DEFAULT).createTables(TableCreationMode.CREATE_NOT_EXISTS)

        // sets up data store
        val config = KotlinConfiguration(dataSource = sqLiteDataSource, model = Models.DEFAULT)
        val dataStore = KotlinEntityDataStore<Persistable>(config)
        return dataStore
    }

    @Provides
    @Singleton
    fun providesUserDao() : Dao<User>{
        val userDao = UserRepo(providesDataStore())
        return userDao
    }

    @Provides
    @Singleton
    fun providesLanguageDao() : Dao<Language>{
        val languageDao = LanguageRepo(providesDataStore())
        return languageDao
    }
}