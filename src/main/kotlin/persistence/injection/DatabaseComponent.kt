package persistence.injection

import dagger.Component
import data.persistence.AppDatabase
import persistence.AppDatabaseImpl
import javax.inject.Singleton

@Component(modules = [DatabaseModule::class])
@Singleton
interface DatabaseComponent {
    fun inject() : AppDatabase
}