package persistence.injection

import dagger.Module
import dagger.Provides
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
import persistence.AppDatabaseImpl
import persistence.mapping.LanguageMapper
import persistence.mapping.UserMapper
import persistence.model.Models
import persistence.repo.LanguageRepo
import persistence.repo.UserRepo
import javax.inject.Singleton

@Module
class DatabaseModule {
    @Provides
    fun providesAppDatabase() : AppDatabase = AppDatabaseImpl.getInstance()
}