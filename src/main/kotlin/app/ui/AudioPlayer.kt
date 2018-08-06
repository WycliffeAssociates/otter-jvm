import javafx.scene.media.AudioClip
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.net.URI
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.TargetDataLine


class AudioPlayer() {

    var currAudioClip: AudioClip? = null

    fun loadAndPlayFromUri(source: URI){
        loadFromUri(source)
        play()
    }

    fun loadFromUri(source: URI) {
        currAudioClip = AudioClip(source.toString())
    }

    fun loadAndPlayAudioClip(clip: AudioClip) {
        currAudioClip = clip
        currAudioClip?.play()
    }

    fun loadFromAudioClip(clip: AudioClip) {
        currAudioClip = clip
    }

    fun play() {
        currAudioClip?.play()
    }

    fun stop() {
        currAudioClip?.stop();
    }
}
