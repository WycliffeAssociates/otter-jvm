import javafx.scene.media.AudioClip
import java.net.URI


class AudioPlayer(audioClip: AudioClip) {

    var currAudioClip: AudioClip? = audioClip

    constructor(sourceString: String): this(AudioClip(sourceString))

    constructor(sourceURI: URI): this(AudioClip(sourceURI.toString()))

    fun play() {
        currAudioClip?.play()
    }

    fun stop() {
        currAudioClip?.stop();
    }
}
