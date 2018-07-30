package app.filesystem

import java.io.File
import java.util.*

// The User's Profile: the audio hash info in one file and an image in another file
class ProfileConfig {
    val locale = Locale.getDefault()
    val labels= ResourceBundle.getBundle("Resources", locale)

    val directoryProvider = DirectoryProvider(labels.getString("Application_Name"))


    // Creates a user subdirectory and stores their hash info
    fun createDirectoryWithUsername(username: String) {

    }

    fun storeImage() {

    }

    // Creates a file in the private internal storage of the given application
    fun createFile(filename: String, append: String): File {
        //create a profile directory in the internal storage of the app to store the user's profile info
        val pathDir = directoryProvider.getAppDataDirectory(append)
        //create the file for the user's name recording
        val filename = File(pathDir, filename)
        filename.createNewFile()
        return filename
    }
}