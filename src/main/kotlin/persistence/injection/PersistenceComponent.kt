package persistence.injection

import dagger.Component
import data.persistence.AppDatabase
import data.persistence.AppPreferences
import filesystem.IDirectoryProvider
import javax.inject.Singleton

@Component(modules = [PersistenceModule::class])
@Singleton
interface PersistenceComponent {
    fun injectDatabase(): AppDatabase
    fun injectPreferences(): AppPreferences
    fun injectDirectoryProvider(): IDirectoryProvider
}