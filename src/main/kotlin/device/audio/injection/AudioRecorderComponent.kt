package device.audio.injection

import dagger.Component
import javax.inject.Singleton
import device.IAudioRecorder

@Component(modules = [AudioRecorderModule::class])
@Singleton
interface AudioRecorderComponent {
    fun inject() : IAudioRecorder
}