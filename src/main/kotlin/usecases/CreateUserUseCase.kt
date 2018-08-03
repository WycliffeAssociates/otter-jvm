package usecases

import data.model.Language
import data.model.User
import data.model.UserPreferences
import persistence.DirectoryProvider
import java.io.File

class CreateUserUseCase {
    private val currentRecording: File? = null
    private var currentImage: File? = null
    private var audioHash = ""
    private var sourceLanguages: List<Language> = emptyList()
    private var targetLanguages: List<Language> = emptyList()
    private var preferredSource: Language? = null
    private var preferredTarget: Language? = null

    // TODO: Do something
    fun startRecording() {
        currentRecording?.delete()
        // todo:
        // currentRecording = File.createTempFile(....)
        // pass to recorder
    }
    fun stopRecording() {
        // generate audio hash
        // audioHash = generate hash
    }
    fun playRecording() {}

    fun setImage(file: File) {
        currentImage?.delete()
        currentImage = file
    }
    fun setImageWithIdenticon(): File {
        // val svgString = generateIdenticon(audioHash)
        currentImage?.delete()
        currentImage = DirectoryProvider("appname").getAppDataDirectory("tmp/profile.svg")
        // write svgString to currentImage
        return currentImage!!
    }

    fun setSourceLangauges(languages: List<Language>) {
        sourceLanguages = languages
    }
    fun setTargetLangauges(languages: List<Language>) {
        targetLanguages = languages
    }

    fun setPreferredSource(language: Language) {
        preferredSource = language
    }
    fun setPreferredTarget(language: Language) {
        preferredTarget = language
    }

    fun commit() {
        // check that source and target preferred are in language lists
        if (!sourceLanguages.contains(preferredSource)) {
            throw NoSuchElementException("Preferred source language does not exist in list of source languages")
        }
        if (!targetLanguages.contains(preferredTarget)) {
            throw NoSuchElementException("Preferred target language does not exist in list of target languages")
        }
        if (currentRecording == null) throw NullPointerException("No audio recording for user")

        val localImage = currentImage?: setImageWithIdenticon()
        val localSource: Language = preferredSource ?:
        throw NullPointerException("No preferred Source has been selected")
        val localTarget: Language = preferredTarget ?:
        throw NullPointerException("No preferred Target has been selected")

        val user = User(
            id = 0,
            audioHash = audioHash,
            audioPath = currentRecording.path,
            imagePath = localImage.path,
            sourceLanguages = sourceLanguages.toMutableList(),
            targetLanguages = targetLanguages.toMutableList(),
            userPreferences = UserPreferences(
                id = 0,
                sourceLanguage = localSource,
                targetLanguage = localTarget
            )
        )
        
    }
}