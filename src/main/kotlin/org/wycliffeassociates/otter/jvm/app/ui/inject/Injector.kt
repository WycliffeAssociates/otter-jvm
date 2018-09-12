package org.wycliffeassociates.otter.jvm.app.ui.inject

import org.wycliffeassociates.otter.jvm.device.audio.injection.DaggerAudioComponent
import org.wycliffeassociates.otter.jvm.persistence.injection.DaggerPersistenceComponent

object Injector {
    val projectDao = DaggerPersistenceComponent
            .builder()
            .build()
            .injectDatabase()
            .getProjectDao()

    // Audio Injection
    private val audioComponent =  DaggerAudioComponent
            .builder()
            .build()

    val audioPlayer
        get() = audioComponent.injectPlayer()
}