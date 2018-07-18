package persistence.repo

import dagger.Component
import javax.inject.Singleton

@Component(modules = [Module::class])
@Singleton
interface Component {
    fun getTrDatabase() : TrDatabaseImpl
}