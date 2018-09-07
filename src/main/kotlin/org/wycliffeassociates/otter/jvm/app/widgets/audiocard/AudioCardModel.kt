package org.wycliffeassociates.otter.jvm.app.widgets.audiocard

import io.reactivex.Observable
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler
import org.wycliffeassociates.otter.common.device.IAudioPlayer
import tornadofx.getProperty
import tornadofx.property
import java.io.File
import java.util.concurrent.TimeUnit

open class AudioCardModel(
        title: String,
        subtitle: String,
        audioFile: File,
        private val audioPlayer: IAudioPlayer,
        isPlaying: Boolean = false
) {
    // Rationale:
    // 1. View model updates the `title` field in this model
    // 2. FX observable backing property catches the update
    // 3. Anything listening to the backing property `titleProperty` gets the update
    var title: String by property(title)
    val titleProperty = getProperty(AudioCardModel::title)

    var subtitle: String by property(subtitle)
    val subtitleProperty = getProperty(AudioCardModel::subtitle)

    var audioFile: File by property(audioFile)
    val audioFileProperty = getProperty(AudioCardModel::audioFile)

    var isPlaying: Boolean by property(isPlaying)
    val isPlayingProperty = getProperty(AudioCardModel::isPlaying)

    var audioProgress: Double by property(0.0)
    val audioProgressProperty = getProperty(AudioCardModel::audioProgress)

    // Business logic/use case
    open fun play() {
        // Stop any existing playback
        audioPlayer.stop()

        // Show loading bar
        audioProgress = -1.0

        // Start playback
        audioPlayer
                .load(audioFile)
                .subscribe {
                    // Save the file duration
                    val audioFileDurationMs = audioPlayer
                            .getAbsoluteDurationMs()
                            .toDouble()

                    // Setup the progress tracking
                    val disposable = Observable
                            .interval(15, TimeUnit.MILLISECONDS)
                            .observeOn(JavaFxScheduler.platform())
                            .subscribe {
                                val elapsedTime = audioPlayer
                                        .getAbsoluteLocationMs()
                                        .toDouble()
                                audioProgress = elapsedTime / audioFileDurationMs
                            }

                    // Play the file
                    audioPlayer.play {
                        audioPlayer.stop() // Stop playing any audio
                        disposable.dispose() // Stops the progress bar update observable
                        isPlaying = false
                        audioProgress = 0.0
                    }
                    isPlaying = true
                }
    }

    fun pause() {
        // Pause playback
        audioPlayer.pause()
        isPlaying = false
    }
}