package persistence.injection

import dagger.Module
import dagger.Provides
import data.persistence.AppDatabase
import persistence.AppDatabaseImpl

// Gives dagger instructions on how to build the database
// Calls this function if no database exists; this function gets the database object (AppDatabaseImpl)
@Module
class DatabaseModule {
    @Provides
    fun providesAppDatabase() : AppDatabase = AppDatabaseImpl
}