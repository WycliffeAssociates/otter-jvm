package app.filesystem

import java.util.*
import data.model.User
import java.io.File
import filesystem.IDirectoryProvider

// The User's Profile: the audio hash info in one file and an image in another file
// Key for DirectoryProvider should be "Application_Name"
class ProfileConfig(val directoryProvider: IDirectoryProvider) {
    val locale = Locale.getDefault()

    // Creates a user subdirectory and stores their hash info
    fun storeAudio(userMine: User, audioFile: File): String {
        val path = createDirectory("Audio")
        return createFile(path, userMine.audioHash + ".wav", audioFile)
    }

    // Stores the image with name of the user who stored it, followed by the original name of the file
    fun storeImage(userMine: User, imageFile: File): String {
        val path = createDirectory("Images")
        return createFile(path, userMine.audioHash + "-" + imageFile.name, imageFile)
    }

    // Returns path of new directory
    fun createDirectory(directoryName: String): String {
        //Todo: prevent illegal paths
        val pathDir = directoryProvider.getAppDataDirectory(directoryName, true)
        println("path: " + pathDir)
        return pathDir
    }

    // Creates a file in the private internal storage of the given application
    fun createFile(path: String, filename: String, fileGiven: File): String {
        val fileMaker = FileMaker()
        return fileMaker.storeFile(path, filename, fileGiven)
    }

    // If we want to use a different method to make files, this will be easy to swap out and test
    class FileMaker {
        fun storeFile(path: String,  filename: String, fileGiven: File): String {
            val fileNew = File(path, filename)
            fileGiven.copyTo(fileNew, true)
            return fileNew.path
        }
    }
}