package org.wycliffeassociates.otter.jvm.persistence.injection

import dagger.Module
import dagger.Provides
import org.wycliffeassociates.otter.common.data.persistence.AppDatabase
import org.wycliffeassociates.otter.common.data.persistence.AppPreferences
import org.wycliffeassociates.otter.jvm.persistence.AppPreferencesImpl
import org.wycliffeassociates.otter.jvm.persistence.DirectoryProvider
import org.wycliffeassociates.otter.common.persistence.IDirectoryProvider

@Module
class PersistenceModule {
    @Provides
    fun providesAppDatabase() : AppDatabase {
        TODO("App database interface and implementation are not finalized")
    }

    @Provides
    fun providesAppPreferences() : AppPreferences = AppPreferencesImpl

    @Provides
    fun providesDirectoryProvider() : IDirectoryProvider = DirectoryProvider("TranslationRecorder")
}