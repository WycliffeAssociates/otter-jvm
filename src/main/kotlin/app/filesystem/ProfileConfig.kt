package app.filesystem

import java.io.File
import java.util.*

class ProfileConfig {
    val locale = Locale.getDefault()
    val labels= ResourceBundle.getBundle("Resources", locale)

    val directoryProvider = DirectoryProvider(labels.getString("Application_Name"))


    //creates a file in the internal storage of the given application to store the user's profile info
    fun createFile(filename: String): File {
        //create a profile directory in the internal storage of the app to store the user's profile info
        val pathDir = directoryProvider.getAppDataDirectory("Profile")
        //create the file for the user's name recording
        val profileFile = File(pathDir, filename)
        profileFile.createNewFile()
        return profileFile
    }
}