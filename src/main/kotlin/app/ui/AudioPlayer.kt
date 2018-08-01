import javafx.scene.media.AudioClip

//TODO: annotate singleton
class AudioPlayer() {

    fun loadAndPlayFromUri(uri: String){
        playAudioClip(loadFromUri(uri))
    }

    fun loadFromUri(uri: String): AudioClip {
        return AudioClip(uri)
    }

    fun playAudioClip(clip: AudioClip) {
        clip.play()
        // TODO: make main thread sleep while audio is playing on its own thread
        /*while(clip.isPlaying) {
            Thread.yield()
        }*/
        println("done playing")
    }
}
