import java.io.File

fun main(args: Array<String>) {
    val audioPlayer = AudioPlayer()
    val file = File("/Users/matthew/Documents/slade.wav")
    audioPlayer.load(file).blockingAwait()
    audioPlayer.play()
    Thread.sleep(1000)
    println("Frames: ${audioPlayer.getAbsoluteDurationInFrames()}")
    println("Ms: ${audioPlayer.getAbsoluteDurationMs()}")
    println("Position Frames: ${audioPlayer.getAbsoluteLocationInFrames()}")
    println("Position Ms: ${audioPlayer.getAbsoluteLocationMs()}")
    audioPlayer.load(File("/Users/matthew/Documents/slade_slow.wav")).blockingAwait()
    audioPlayer.play()
    Thread.sleep(5000)
}