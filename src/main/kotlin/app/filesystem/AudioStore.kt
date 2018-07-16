package app.filesystem

import java.io.File
import java.nio.file.FileSystems
import java.util.regex.Pattern

class AudioStore (private val projectName: String) {

    val directoryProvider = DirectoryProvider("translationRecorder")

    private val separator = FileSystems.getDefault().separator

    private val path = "Projects" + separator + projectName + separator

    //given a file name for a take, creates an audio file for that take in its project folder
    fun createTakeFile(fileString: String): File {
        val pathDir = directoryProvider.getUserDataDirectory(path, true)
        val takeDirs = File(pathDir)
        //make project directory to store take files in
        takeDirs.mkdirs()
        val takeFile = File(takeDirs, fileString)
        takeFile.createNewFile()
        return takeFile
    }

    //given a file, checks that it is in the correct location and follows the naming convention for takes
    fun corrLocation(audioTake: File): Boolean {
        val absPath = audioTake.absolutePath
        return Pattern.matches(directoryProvider.getUserDataDirectory(path, true) +
                "take\\d*_\\d*.wav", absPath)
    }
}