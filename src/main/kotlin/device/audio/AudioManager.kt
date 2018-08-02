package device.audio
import AudioPlayer

class AudioManager {

    val audioPlayer = AudioPlayer()
    val wavRecorder = WavRecorder

    fun recordAndPlayBack(filename: String) {
        println("start")
        val tmp = wavRecorder.record(filename)
        /*println("stop")
        wavRecorder.stop()
        println("playback")
        audioPlayer.loadAndPlayFromUri(tmp.toURI().toString())
        println(tmp.toURI().toString())
        println("done")*/
    }
}