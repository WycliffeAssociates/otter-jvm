package org.wycliffeassociates.otter.jvm.app.widgets.audiocard

import tornadofx.ViewModel

open class AudioCardViewModel (private val model: AudioCardModel) : ViewModel() {
    // bind to model properties to expose to view
    val titleProperty = bind { model.titleProperty }
    val subtitleProperty = bind { model.subtitleProperty }
    val audioProgressProperty = bind { model.audioProgressProperty }
    val isPlayingProperty = bind { model.isPlayingProperty }

    open fun playPauseButtonPressed() {
        if (model.isPlaying) {
            model.pause()
        } else {
            model.play()
        }
    }
}