package org.wycliffeassociates.otter.jvm.device.audio

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import org.wycliffeassociates.otter.common.device.AudioPlayerEvent
import java.io.File
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import org.wycliffeassociates.otter.common.device.IAudioPlayer
import org.wycliffeassociates.otter.common.device.IAudioPlayerListener

class AudioPlayer: IAudioPlayer {

    override fun addEventListener(listener: IAudioPlayerListener) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addEventListener(onEvent: (event: AudioPlayerEvent) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var clip: Clip = AudioSystem.getClip()

    override fun addEventListener(onEvent: (event: AudioPlayerEvent) -> Unit) {
        TODO("not implemented")
    }

    override fun addEventListener(listener: IAudioPlayerListener) {
        TODO("not implemented")
    }

    override fun load(file: File): Completable {
        pause()
        if (clip.isOpen) clip.close()
        clip = AudioSystem.getClip()
        val audioInputStream = AudioSystem.getAudioInputStream(file)
        return Completable.fromAction {
            clip.open(audioInputStream)
        }.subscribeOn(Schedulers.io())
    }

    override fun play() {
        if (!clip.isRunning) clip.start()
    }

    override fun pause() {
        if (clip.isRunning) clip.stop()
    }

    override fun stop() {
        pause()
        clip.framePosition = 0
    }

    override fun getAbsoluteDurationInFrames(): Int {
        return clip.frameLength
    }

    override fun getAbsoluteDurationMs(): Int {
        return (getAbsoluteDurationInFrames() / 44.1).toInt()
    }

    override fun getAbsoluteLocationInFrames(): Int {
        return clip.framePosition
    }

    override fun getAbsoluteLocationMs(): Int {
        return (getAbsoluteLocationInFrames() / 44.1).toInt()
    }
}