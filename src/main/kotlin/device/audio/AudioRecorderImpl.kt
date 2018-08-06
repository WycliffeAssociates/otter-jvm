package device.audio

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.TargetDataLine
import device.IAudioRecorder

class AudioRecorderImpl : IAudioRecorder {
    companion object {
        val SAMPLE_RATE = 44100F // Hz
        val SAMPLE_SIZE = 16 // bits
        val CHANNELS = 1
        val SIGNED = true
        val BIG_ENDIAN = false
        val FORMAT = AudioFormat(
                SAMPLE_RATE,
                SAMPLE_SIZE,
                CHANNELS,
                SIGNED,
                BIG_ENDIAN
        )
    }
    private var line: TargetDataLine
    private val audioByteObservable = PublishSubject.create<ByteArray>()
    init {
        line = AudioSystem.getTargetDataLine(FORMAT)
    }
    fun start() {
        line.open(FORMAT)
        line.start()
        Observable.fromCallable {
            val byteArray = ByteArray(1024)
            var totalRead = 0
            while (line.isOpen) {
                totalRead += line.read(byteArray, 0, byteArray.size)
                audioByteObservable.onNext(byteArray)
            }
        }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }
    fun stop() {
        line.stop()
        line.close()
    }
    fun getAudioStream(): Observable<ByteArray> {
        return audioByteObservable
    }
}
