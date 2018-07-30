package app.filesystem

import data.model.User
import java.io.File
import java.nio.file.FileSystems
import java.util.*

// The User's Profile: the audio hash info in one file and an image in another file
class ProfileConfig {
    val locale = Locale.getDefault()
    val labels= ResourceBundle.getBundle("Resources", locale)

    val directoryProvider = DirectoryProvider(labels.getString("Application_Name"))

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

    // Returns path of new direcotry
    fun createDirectory(directoryName: String): String {
        val pathDir = directoryProvider.getPrivateAppDataDirectory()
        val path = pathDir + FileSystems.getDefault().separator + directoryName
        directoryProvider.makeDirectories(path);
        return path;
    }

    // Creates a file in the private internal storage of the given application
    fun createFile(path: String, filename: String): File {
        //create the file for the user's name recording
        val filename = File(path, filename)
        filename.createNewFile()
        return filename
    }
}