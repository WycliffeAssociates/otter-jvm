package org.wycliffeassociates.otter.jvm.app.widgets.audiocard

import io.reactivex.Observable
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import org.wycliffeassociates.otter.common.device.IAudioPlayer
import java.util.concurrent.TimeUnit

open class AudioCardViewModel(
        private val model: AudioCardModel,
        private val audioPlayer: IAudioPlayer
) {
    val audioProgressProperty = SimpleDoubleProperty(0.0)
    val isPlayingProperty = SimpleBooleanProperty(false)
    val titleProperty = SimpleStringProperty(model.title)
    val subtitleProperty = SimpleStringProperty(model.subtitle)

    open fun playPauseButtonPressed() {
        if (isPlayingProperty.get()) {
            // Pause playback
            audioPlayer.pause()
            model.isPlaying = false
            isPlayingProperty.set(model.isPlaying)
        } else {
            // Start playback
            audioPlayer
                    .load(model.audioFile)
                    .observeOn(JavaFxScheduler.platform())
                    .subscribe {
                        // Save the file duration
                        val audioFileDurationMs = audioPlayer
                                .getAbsoluteDurationMs()
                                .toDouble()
                        // Setup the progress tracking
                        val disposable = Observable
                                .interval(15, TimeUnit.MILLISECONDS)
                                .subscribe {
                                    val elapsedTime = audioPlayer
                                            .getAbsoluteLocationMs()
                                            .toDouble()
                                    audioProgressProperty.set(elapsedTime / audioFileDurationMs)
                                }
                        audioPlayer.play {
                            audioPlayer.stop() // Stop playing any audio
                            disposable.dispose() // Stops the progress bar update observable
                            model.isPlaying = false
                            isPlayingProperty.set(model.isPlaying)
                            audioProgressProperty.set(0.0)
                        }
                        model.isPlaying = true
                        isPlayingProperty.set(model.isPlaying)
                    }
        }
    }
}