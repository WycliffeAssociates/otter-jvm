import javafx.scene.media.AudioClip
import java.io.File

fun main(args: Array<String>) {
    val file = File("WycliffeTest.wav")
    playKotlin(file.toURI().toString());

    // Audio playing happens on another thread so we need to sleep Main's thread for long enough for it to play
    Thread.sleep(10000)
}

fun playKotlin(str: String) {
    val myClip = AudioClip(str)
    myClip.play();
}