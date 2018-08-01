package device.audio

import javafx.application.Application.launch
import kotlinx.coroutines.experimental.launch
import java.io.File
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.TargetDataLine

object WavRecorder {

    val format: AudioFormat
    lateinit var line: TargetDataLine
    val info: DataLine.Info
    @Volatile var isRecording = false

    init {
        format = AudioFormat(44100f, 16, 1, true, false)
        info = DataLine.Info(TargetDataLine::class.java, format)
        if (!AudioSystem.isLineSupported(info)) {
            println("oh noes")
        }
        try {
            line = AudioSystem.getLine(info) as TargetDataLine
            line.open(format)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun record(output: String): File {
        val recording = File(output)
        //recording.createNewFile()
        return record(recording)
    }

    fun record(output: File): File {
        launch {
            output.createNewFile()
            output.writeText("")
            var numBytesRead = 0
            var buffer = ByteArray(1024)
            isRecording = true
            line.start()
            while (numBytesRead != -1 && line.isActive) {
                numBytesRead = line.read(buffer, 0, buffer.size)
                output.appendBytes(buffer)
            }
            println("done")
        }
        println(output.name)
        return output
    }

    fun stop() {
        line.stop()
    }
}