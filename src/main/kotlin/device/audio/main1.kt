package device.audio

fun main(args:Array<String>) {
    val audioManager = AudioManager()
    audioManager.recordAndPlayBack("test.wav")
}
