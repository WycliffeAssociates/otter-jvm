package device.audio.injection

import dagger.Module
import dagger.Provides
import device.audio.AudioRecorderImpl
import device.IAudioRecorder

@Module
class AudioRecorderModule {
    @Provides
    fun providesAudioRecorder() : IAudioRecorder = AudioRecorderImpl
}