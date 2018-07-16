package app.filesystem

import java.io.File
import java.nio.file.FileSystems
import java.util.regex.Pattern

class AudioStore (private val projectName: String) {

    val directoryProvider = DirectoryProvider("translationRecorder")

    private val path = "Projects" + FileSystems.getDefault().separator + projectName + FileSystems.getDefault().separator

    //given a file name for a take, creates an audio file for that take in its project folder
    fun createTakeFile(fileString: String): File {
        val pathDir = directoryProvider.getUserDataDirectory(path, true)
        val takeDirs = File(pathDir)
        takeDirs.mkdirs()
        val takeFile = File(takeDirs, fileString)
        takeFile.createNewFile()
        return takeFile
    }

    fun corrLocation(audioTake: File): Boolean {
        val absPath = audioTake.absolutePath
        return Pattern.matches(directoryProvider.getUserDataDirectory(path, true) +
                "take\\d.wav", absPath)
    }
}