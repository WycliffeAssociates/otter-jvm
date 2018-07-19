package persistence.injection

import dagger.Component
import persistence.AppDatabaseImpl
import javax.inject.Singleton

@Component(modules = [DaoModule::class])
@Singleton
interface DaoComponent {
    fun getAppDatabase() : AppDatabaseImpl
}