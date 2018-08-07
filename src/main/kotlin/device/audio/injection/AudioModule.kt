package device.audio.injection

import dagger.Module
import dagger.Provides
import device.audio.AudioRecorderImpl
import device.IAudioRecorder
import device.audio.AudioPlayer
import javax.inject.Singleton

@Module
class AudioModule {
    @Provides
    fun providesRecorder(): IAudioRecorder = AudioRecorderImpl()

    @Provides
    @Singleton
    fun providesPlayer(): IAudioPlayer = AudioPlayer()
}