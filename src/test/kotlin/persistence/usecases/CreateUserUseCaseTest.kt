package persistence.usecases

import data.model.Language
import data.model.User
import data.model.UserPreferences
import device.audio.AudioPlayer
import device.audio.AudioRecorderImpl
import device.audio.injection.DaggerAudioComponent
import io.reactivex.Observable
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import persistence.data.LanguageStore
import persistence.injection.DaggerPersistenceComponent
import persistence.repo.UserRepo
import usecases.CreateUserUseCase

@RunWith(PowerMockRunner::class)
@PrepareForTest(DaggerPersistenceComponent::class, DaggerAudioComponent::class)
class CreateUserUseCaseTest {
    private val mockPersistenceBuilder = Mockito.mock(
        DaggerPersistenceComponent.Builder::class.java,
        Mockito.RETURNS_DEEP_STUBS)
    private val mockUserDao = Mockito.mock(UserRepo::class.java, Mockito.RETURNS_DEEP_STUBS)
    // TODO: mock svg generator

    @Before
    fun setup() {
        PowerMockito.mockStatic(DaggerPersistenceComponent::class.java)
        Mockito
            .`when`(DaggerPersistenceComponent.builder())
            .thenReturn(mockPersistenceBuilder)
        Mockito
            .`when`(mockPersistenceBuilder
                .build()
                .injectDatabase()
                .getUserDao()
            ).thenReturn(mockUserDao)

        // TODO: mock svg generation
    }

    @Test(expected = NullPointerException::class)
    fun testIfCommitFailsWithoutRecording(){
        val createUseCase = CreateUserUseCase()

        createUseCase.setImageWithIdenticon()
        createUseCase.addSourceLanguage(LanguageStore.getById(1))
        createUseCase.addTargetLanguage(LanguageStore.getById(2))
        createUseCase.setPreferredSource(LanguageStore.getById(1))
        createUseCase.setPreferredTarget(LanguageStore.getById(2))

        createUseCase.commit()
    }

    @Test(expected = NoSuchElementException::class)
    fun testIfSettingPreferredSourceFailsIfSourceNotInSourceLanguages(){
        val createUseCase = CreateUserUseCase()

        createUseCase.addSourceLanguage(LanguageStore.getById(1))
        createUseCase.setPreferredSource(LanguageStore.getById(3))
    }

    @Test(expected = NoSuchElementException::class)
    fun testIfSettingPreferredSourceFailsIfTargetNotInTargetLanguages(){
        val createUseCase = CreateUserUseCase()

        createUseCase.addTargetLanguage(LanguageStore.getById(2))
        createUseCase.setPreferredSource(LanguageStore.getById(3))
    }

    @Test(expected = NullPointerException::class)
    fun testIfGetImageFailsIfImageNotSet(){
        val createUseCase = CreateUserUseCase()

        createUseCase.getImage()
    }

    @Test(expected = NullPointerException::class)
    fun testIfSetImageFailsIfRecordingNotSet(){
        val createUseCase = CreateUserUseCase()
        createUseCase.setImageWithIdenticon()
    }

    @Test(expected = NullPointerException::class)
    fun testIfCommitFailsIfPreferredSourceNotSet(){
        val createUseCase = CreateUserUseCase()

        createUseCase.startRecording()
        createUseCase.stopRecording()
        createUseCase.setImageWithIdenticon()
        createUseCase.addSourceLanguage(LanguageStore.getById(1))
        createUseCase.addTargetLanguage(LanguageStore.getById(2))
        createUseCase.setPreferredTarget(LanguageStore.getById(2))

        createUseCase.commit()
    }

    @Test(expected = NullPointerException::class)
    fun testIfCommitFailsIfPreferredTargetNotSet(){
        val createUseCase = CreateUserUseCase()

        createUseCase.startRecording()
        createUseCase.stopRecording()
        createUseCase.setImageWithIdenticon()
        createUseCase.addSourceLanguage(LanguageStore.getById(1))
        createUseCase.addTargetLanguage(LanguageStore.getById(2))
        createUseCase.setPreferredSource(LanguageStore.getById(2))

        createUseCase.commit()
    }
}