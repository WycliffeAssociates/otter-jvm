import api.ui.AudioMine
import java.io.File

fun main(args: Array<String>) {
    val file = File("WycliffeTest.wav")
    val audio = AudioMine(file.toURI().toString())
    audio.play()

    // Audio playing happens on another thread so we need to sleep Main's thread for long enough for it to play
    Thread.sleep(10000)
}