package app.filesystem

import java.io.File
import java.nio.file.FileSystems
import java.util.*
import java.util.regex.Pattern

class AudioStore (val type: String) {
    val locale = Locale.getDefault()
    //val labels= ResourceBundle.getBundle("Resources", locale)

    val directoryProvider = DirectoryProvider("translationRecorder")

    private val separator = FileSystems.getDefault().separator

    //given a file name for a take, creates an audio file for that take in its project folder
    fun createTakeFile(lang: String, anth: String, book: String, chap: String, chunk: String, take: String): File {
        val path = lang + separator + anth + separator + book + separator + chap + separator + chunk + separator
        val pathDir = directoryProvider.getUserDataDirectory(path, true)
        val takeDirs = File(pathDir)
        val fileSep = "_"
        //make project directory to store take files in
        takeDirs.mkdirs()
        val takeFile = File(takeDirs, "take"+fileSep+lang+fileSep+anth+fileSep+book+fileSep+chap+fileSep+chunk+fileSep+take+".wav")
        //create the audio take file with the given name
        takeFile.createNewFile()
        return takeFile
    }

    //given a file, checks that it is in the correct location and follows the naming convention for takes
    /*fun checkLocationAndName(audioTake: File): Boolean {
        val absPath = audioTake.absolutePath
        //for now, assume audio take files follow convention "take{chunk number}_{take number of that chunk}.wav"
        return Pattern.matches(directoryProvider.getUserDataDirectory(path, true) +
                "take\\d*_\\d*.wav", absPath)
    }*/

    fun createProfileFile(): File{
        val pathDir = directoryProvider.getAppDataDirectory("Profile", true)
        val profileDirs = File(pathDir)
        profileDirs.mkdirs()
        val profileFile = File(profileDirs, "profile_recording.wav")
        profileFile.createNewFile()
        return profileFile
    }
}