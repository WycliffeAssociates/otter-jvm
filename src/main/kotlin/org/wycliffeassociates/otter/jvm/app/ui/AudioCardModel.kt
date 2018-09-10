package org.wycliffeassociates.otter.jvm.app.ui

import tornadofx.*
import java.io.File

open class AudioCardModel(title: String, audioFile: File) {
    var title: String by property(title)
    val titleProperty = getProperty(AudioCardModel::title)

    var audioFile: File by property(audioFile)
    val audioFileProperty = getProperty(AudioCardModel::audioFile)

    open fun play() {
        // play the audio file
    }
}