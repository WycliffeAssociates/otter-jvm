package org.wycliffeassociates.otter.jvm.device.audioplugin.injection

import dagger.Module
import dagger.Provides
import org.wycliffeassociates.otter.common.data.persistence.AppDatabase
import org.wycliffeassociates.otter.common.domain.IAudioPluginRegistrar
import org.wycliffeassociates.otter.jvm.device.audioplugin.AudioPluginRegistrar
import org.wycliffeassociates.otter.jvm.persistence.injection.PersistenceModule

@Module(includes = [PersistenceModule::class])
class AudioPluginModule {
    @Provides
    fun providesRegistrar(appDatabase: AppDatabase): IAudioPluginRegistrar = AudioPluginRegistrar(
            appDatabase.getAudioPluginDataDao()
    )
}