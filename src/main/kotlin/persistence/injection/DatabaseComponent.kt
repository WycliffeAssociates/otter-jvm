package persistence.injection

import dagger.Component
import data.persistence.AppDatabase
import javax.inject.Singleton

// We call this to get the database from dagger
@Component(modules = [DatabaseModule::class])
// There will only ever be one db component (responsible for injection of the db) at a time
@Singleton
interface DatabaseComponent {
    fun inject() : AppDatabase
}