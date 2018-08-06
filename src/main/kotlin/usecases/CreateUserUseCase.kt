package usecases

import data.model.Language
import data.model.User
import data.model.UserPreferences
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import persistence.DirectoryProvider
import persistence.injection.DaggerPersistenceComponent
import persistence.injection.PersistenceComponent
import persistence.injection.PersistenceModule
import java.io.File
import java.io.FileInputStream

class CreateUserUseCase {
    private val currentRecording: File? = null
    private var currentImage: File? = null
    private var audioHash = ""
    private val sourceLanguages: MutableList<Language> = mutableListOf()
    private val targetLanguages: MutableList<Language> = mutableListOf()
    private var preferredSource: Language? = null
    private var preferredTarget: Language? = null
    // TODO: inject audio player

    // TODO: implement
    fun startRecording() {
        currentRecording?.delete()
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

    fun setImageWithIdenticon(){
        // val svgString = generateIdenticon(audioHash)
        currentImage?.delete()
        // calls device to generate Identicon
        // stores in location

        currentImage = DirectoryProvider("appname")
            .getAppDataDirectory("${generateIdenticonString()}/profile.svg")
        currentImage?.printWriter()
            .use {
                it?.println(audioHash)
            }
    }

    private fun generateIdenticonString(): String {
        // ensures that something has been recorded
        if (currentRecording == null){
            throw NullPointerException("Recording has not been set")
        }
        audioHash = String(Hex.encodeHex(DigestUtils.md5(FileInputStream(currentRecording))))
        return audioHash
    }

    fun getImage(): File = currentImage?: throw NullPointerException("Image Has not been set")

    fun addSourceLanguage(language: Language) {
        sourceLanguages.add(language)
    }

    fun removeSourceLanguage(language: Language){
        sourceLanguages.remove(language)
        // updates preferred source
        if (preferredSource == language){
            preferredSource = null
        }
    }

    fun addTargetLanguage(language: Language) {
        targetLanguages.add(language)
    }

    fun removeTargetLanguage(language: Language){
        targetLanguages.remove(language)
        // updates preferred target
        if(preferredTarget == language){
            preferredTarget = null
        }
    }

    fun setPreferredSource(language: Language) {
        // enforces constraint that preferred must be within list of languages
        if (!sourceLanguages.contains(preferredSource)) {
            throw NoSuchElementException("Preferred source language does not exist in list of source languages")
        }
        preferredSource = language
    }

    fun setPreferredTarget(language: Language) {
        if (!targetLanguages.contains(preferredTarget)) {
            throw NoSuchElementException("Preferred target language does not exist in list of target languages")
        }
        preferredTarget = language
    }

    fun commit(): Observable<User> {
        val user = User(
            id = 0,
            audioHash = audioHash,
            audioPath = currentRecording?.path ?: throw NullPointerException("No audio recording for user"),
            imagePath = currentImage?.path ?: getImage().path,
            sourceLanguages = sourceLanguages,
            targetLanguages = targetLanguages,
            userPreferences = UserPreferences(
                id = 0,
                sourceLanguage = preferredSource ?:
                throw NullPointerException("No preferred Source has been selected"),
                targetLanguage = preferredTarget ?:
                throw NullPointerException("No preferred Target has been selected")
            )
        )

        val userDao = DaggerPersistenceComponent
            .builder()
            .build()
            .injectDatabase()
            .getUserDao()

        return Observable.create<User> {
            user.id = userDao.insert(user).blockingFirst()
            it.onNext(user)
        }.subscribeOn(Schedulers.io())
    }
}