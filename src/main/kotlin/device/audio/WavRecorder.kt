package device.audio

import javafx.application.Application.launch
import kotlinx.coroutines.experimental.launch
import java.io.File
import javax.sound.sampled.*
import org.wycliffeassociates.tr.wav.WavFile
import org.wycliffeassociates.tr.wav.WavOutputStream

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
            line.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun record(output: String): WavFile {
        val recording = File(output)
        return record(recording)
    }

    fun record(output: File): WavFile {
        val recording = WavFile(output)
        val recordingOutputStream = WavOutputStream(recording)
        //output.writeText("")
        var numBytesRead = 0
        var buffer = ByteArray(1024)
        isRecording = true
        val startTime = System.currentTimeMillis()
        while (false or ((System.currentTimeMillis()-startTime)<3000)) {
            println("recording")
            numBytesRead = line.read(buffer, 0, buffer.size)
            recordingOutputStream.write(buffer)
            //output.appendBytes(buffer)
        }
        println("done")
        println(isRecording)
        return recording
    }

    fun stop() {
        line.stop()
    }
}