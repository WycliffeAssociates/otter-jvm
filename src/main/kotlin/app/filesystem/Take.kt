package app.filesystem

import java.io.File
import java.nio.file.FileSystems
import java.util.*

class Take (lang:String, anth: String, book: String, chap: String, chunk: String, take: String){

    val locale = Locale.getDefault()
    val labels= ResourceBundle.getBundle("Resources", locale)
    val directoryProvider = DirectoryProvider(labels.getString("Application_Name"))

    val fileSep = "_"
    val filename = "take"+fileSep+lang+fileSep+anth+fileSep+book+fileSep+chap+fileSep+chunk+fileSep+take+".wav"

    val separator = FileSystems.getDefault().separator
    val path = lang + separator + anth + separator + book + separator + chap + separator + chunk

    fun createFile(): File{
        val pathDir = directoryProvider.getPublicDataDirectory();
        val takeFile = File(pathDir + path, filename)
        //create the audio take file with the given name
        takeFile.createNewFile()
        return takeFile
    }
}