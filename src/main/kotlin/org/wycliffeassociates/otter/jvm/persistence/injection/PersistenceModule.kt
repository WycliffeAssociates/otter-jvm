package org.wycliffeassociates.otter.jvm.persistence.injection

import dagger.Module
import dagger.Provides
import org.wycliffeassociates.otter.common.data.persistence.IAppDatabase
import org.wycliffeassociates.otter.common.data.persistence.IAppPreferences
import org.wycliffeassociates.otter.jvm.persistence.AppPreferences
import org.wycliffeassociates.otter.jvm.persistence.DirectoryProvider
import org.wycliffeassociates.otter.common.persistence.IDirectoryProvider

@Module
class PersistenceModule {
    @Provides
    fun providesAppDatabase() : IAppDatabase {
        TODO("App database interface and implementation are not finalized")
    }

    @Provides
    fun providesAppPreferences() : IAppPreferences = AppPreferences

    @Provides
    fun providesDirectoryProvider() : IDirectoryProvider = DirectoryProvider("TranslationRecorder")
}