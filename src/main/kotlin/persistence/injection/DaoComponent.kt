package persistence.injection

import dagger.Component
import persistence.TrDatabaseImpl
import javax.inject.Singleton

@Component(modules = [DaoModule::class])
@Singleton
interface DaoComponent {
    fun getTrDatabase() : TrDatabaseImpl
}