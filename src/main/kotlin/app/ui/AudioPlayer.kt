import javafx.scene.media.AudioClip

fun playKotlin(URI: String) {
    val myClip = AudioClip(URI)
    myClip.play()
    while(myClip.isPlaying) {
        Thread.yield()
    }
    println("done playing")
}