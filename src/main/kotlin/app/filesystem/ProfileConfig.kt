package app.filesystem

import data.model.User
import filesystem.IDirectoryProvider
import java.io.File
import java.util.*

// The User's Profile: the audio hash info in one file and an image in another file
// Key for DirectoryProvider should be "Application_Name"
class ProfileConfig(val directoryProvider: IDirectoryProvider) {
    val locale = Locale.getDefault()

    // Creates a user subdirectory and stores their hash info
    fun storeAudio(userMine: User, audioFile: File) {
        val path = createDirectory("Audio")
        createFile(path, userMine.audioHash + ".wav")
    }

    // Stores the image with name of the user who stored it, followed by the original name of the file
    fun storeImage(userMine: User, imageFile: File) {
        val path = createDirectory("Images")
        createFile(path, userMine.audioHash + "-" + imageFile.name)
    }

    // Returns path of new directory
    fun createDirectory(directoryName: String): String {
        val pathDir = directoryProvider.getAppDataDirectory(directoryName, true)
        return pathDir
    }

    // Creates a file in the private internal storage of the given application
    fun createFile(path: String, filename: String): File {
        //create the file for the user's name recording
        val file = File(path, filename)
        file.createNewFile()
        return file
    }
}