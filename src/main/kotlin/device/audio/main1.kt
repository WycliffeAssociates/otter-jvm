package device.audio


fun main(args:Array<String>) {
    val wavRecorder = WavRecorder
    wavRecorder.record("test.wav")
}
