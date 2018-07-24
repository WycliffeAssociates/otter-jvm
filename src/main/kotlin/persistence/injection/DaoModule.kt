package persistence.injection

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
import persistence.mapping.LanguageMapper
import persistence.mapping.UserMapper
import persistence.model.Models
import persistence.repo.LanguageRepo
import persistence.repo.UserRepo
import javax.inject.Singleton

@Module
class DaoModule {
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
    fun providesUserDao(dataStore: KotlinEntityDataStore<Persistable>, userMapper: UserMapper) : Dao<User> {
        return UserRepo(dataStore, userMapper)
    }

    @Provides
    @Singleton
    fun providesLanguageDao(dataStore: KotlinEntityDataStore<Persistable>, languageMapper: LanguageMapper) : Dao<Language> {
        return LanguageRepo(dataStore, languageMapper)
    }
}