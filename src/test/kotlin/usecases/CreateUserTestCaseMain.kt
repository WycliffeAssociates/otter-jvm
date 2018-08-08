package usecases

import device.audio.AudioPlayer
import device.audio.AudioRecorderImpl
import persistence.data.LanguageStore
import persistence.injection.DaggerPersistenceComponent
import usecases.CreateUserUseCase
import usecases.InitializeLanguageDatabaseUseCase

fun main(args: Array<String>){
    val createUseCase = CreateUserUseCase()
    val languageDao = DaggerPersistenceComponent
        .builder()
        .build()
        .injectDatabase()
        .getLanguageDao()

    val languages = createUseCase.getLanguages().blockingFirst()

    createUseCase.startRecording()
    Thread.sleep(3000)
    createUseCase.stopRecording()
    createUseCase.playRecording()
    createUseCase.setImageWithIdenticon()
    createUseCase.addSourceLanguage(languages.get(0))
    createUseCase.addTargetLanguage(languages.get(1))
    createUseCase.setPreferredSource(languages.get(0))
    createUseCase.setPreferredTarget(languages.get(1))

    val user = createUseCase.commit().blockingFirst()
    println("id: ${user.id}")
    println("Audio Hash: ${user.audioHash}")
    println("Audio Path: ${user.audioPath}")
    println("Image Path: ${user.imagePath}")
    println("Source Languages: ${user.sourceLanguages}")
    println("Target Languages: ${user.targetLanguages}")
    println("Preferred Source: ${user.userPreferences.sourceLanguage}")
    println("Preferred Target: ${user.userPreferences.targetLanguage}")
    createUseCase.playRecording()
    Thread.sleep(3000)
}