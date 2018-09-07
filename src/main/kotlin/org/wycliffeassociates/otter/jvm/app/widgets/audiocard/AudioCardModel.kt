package org.wycliffeassociates.otter.jvm.app.widgets.audiocard

import java.io.File

open class AudioCardModel(
        val title: String,
        val subtitle: String,
        val audioFile: File,
        var isPlaying: Boolean = false
)