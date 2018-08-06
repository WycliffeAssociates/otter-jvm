import javafx.scene.media.AudioClip
import java.net.URI


class AudioPlayer() {

    var currAudioClip: AudioClip? = null

    //tested on windows
    fun loadAndPlayFromUri(source: URI){
        loadFromUri(source)
        play()
    }

    //tested on windows
    fun loadFromUri(source: URI) {
        currAudioClip = AudioClip(source.toString())
    }

    //tested on windows
    fun loadAndPlayAudioClip(clip: AudioClip) {
        currAudioClip = clip
        currAudioClip?.play()
    }

    //tested on windows
    fun loadFromAudioClip(clip: AudioClip) {
        currAudioClip = clip
    }

    //tested for uri and audioclip on windows
    fun play() {
        currAudioClip?.play()
    }

    //tested for uri and audioclip on windows
    fun stop() {
        currAudioClip?.stop();
    }
}
