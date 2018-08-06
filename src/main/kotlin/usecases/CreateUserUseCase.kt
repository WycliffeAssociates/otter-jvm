package usecases

import data.model.Language
import data.model.User
import data.model.UserPreferences
import device.audio.injection.DaggerAudioComponent
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.apache.commons.codec.binary.Hex
import org.apache.commons.codec.digest.DigestUtils
import persistence.injection.DaggerPersistenceComponent
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileInputStream
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem

class CreateUserUseCase {
    private var currentRecording: File? = null
    private var currentImage: File? = null
    private var audioHash = ""
    private val sourceLanguages: MutableList<Language> = mutableListOf()
    private val targetLanguages: MutableList<Language> = mutableListOf()
    private var preferredSource: Language? = null
    private var preferredTarget: Language? = null
    private val audioBytes = emptyList<Byte>().toMutableList()
    private val databaseComponent = DaggerPersistenceComponent
        .builder()
        .build()
        .injectDatabase()
    private val directoryProvider = DaggerPersistenceComponent
        .builder()
        .build()
        .injectDirectoryProvider()
    private val audioRecorder = DaggerAudioComponent
        .builder()
        .build()
        .injectRecorder()
    private val audioPlayer = DaggerAudioComponent
        .builder()
        .build()
        .injectPlayer()

    init {
        audioRecorder
            .getAudioStream()
            .subscribe {
                audioBytes.addAll(it.toList())
            }
    }

    // TODO: implement
    fun startRecording() {
        currentRecording?.delete()
        audioBytes.clear()
        audioRecorder.start()
    }

    fun stopRecording() {
        audioRecorder.stop()
        currentRecording = createTempFile(prefix = "tmp", suffix = ".wav")
        // writes the bytes to the temp file
        val audioStream = AudioInputStream(ByteArrayInputStream(
            audioBytes.toByteArray()),
            AudioFormat(44100F,16,1,true,false),
            audioBytes.size.toLong()
        )
        AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, currentRecording)
        generateHash()
    }

    fun playRecording() {
        audioPlayer.load(currentRecording?: throw NullPointerException("Recording has not been set")).blockingAwait()
        audioPlayer.play()
    }

    fun setImage(file: File) {
        currentImage?.delete()
        currentImage = file
    }

    fun setImageWithIdenticon(){
        // ensures that something has been recorded to generate identicon
        if (currentRecording == null){
            throw NullPointerException("Recording has not been set")
        }
        currentImage?.delete()
        currentImage = File(directoryProvider.userProfileImageDirectory, "$audioHash.svg")
        currentImage
            ?.printWriter()
            .use {
                it?.println(audioHash)
            }
    }

    private fun generateHash(): String {
        audioHash = String(Hex.encodeHex(DigestUtils.md5(FileInputStream(currentRecording))))
        return audioHash
    }

    fun getImage(): File = currentImage?: throw NullPointerException("Image Has not been set")

    fun getLanguages(): Observable<List<Language>> {
        return databaseComponent.getLanguageDao().getAll()
    }

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
        if (!sourceLanguages.contains(language)) {
            throw NoSuchElementException("Preferred source language does not exist in list of source languages")
        }
        preferredSource = language
    }

    fun setPreferredTarget(language: Language) {
        if (!targetLanguages.contains(language)) {
            throw NoSuchElementException("Preferred target language does not exist in list of target languages")
        }
        preferredTarget = language
    }

    fun commit(): Observable<User> {
        val user = User(
            audioHash = audioHash,
            audioPath = commitRecording(),
            imagePath = currentImage?.path ?: getImage().path,
            sourceLanguages = sourceLanguages,
            targetLanguages = targetLanguages,
            userPreferences = UserPreferences(
                sourceLanguage = preferredSource ?:
                throw NullPointerException("No preferred Source has been selected"),
                targetLanguage = preferredTarget ?:
                throw NullPointerException("No preferred Target has been selected")
            )
        )


        val userDao = databaseComponent.getUserDao()

        return Observable.create<User> {
            user.id = userDao.insert(user).blockingFirst()
            user.userPreferences.id = user.id
            it.onNext(user)
        }.subscribeOn(Schedulers.io())
    }

    /**
     * helper function that converts
     * the temp recording file to an actual one
     * and returns the path of that file
     */
    private fun commitRecording(): String{
        val commitAudio: File

        if(currentRecording != null) {
            commitAudio = File(directoryProvider.userProfileAudioDirectory, "$audioHash.wav")
            commitAudio
                .printWriter()
                .print(currentRecording?.readText())
            currentRecording?.delete()
        } else {
            throw NullPointerException("No recording has been set")
        }

        return commitAudio.path
    }
}