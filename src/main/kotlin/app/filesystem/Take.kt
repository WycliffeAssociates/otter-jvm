package app.filesystem

import data.model.*
import java.io.File
import java.nio.file.FileSystems
import java.util.*

class Take (lang:Language, anth: Anthology, book: Book, chap: Chapter, chunk: Chunk, take: String){

    val sLang = lang.toString()
    val sAnth = anth.toString()
    val sBook = book.toString()
    val sChap = chap.toString()
    val sChunk = chunk.toString()

    val locale = Locale.getDefault()
    val labels= ResourceBundle.getBundle("Resources", locale)
    val directoryProvider = DirectoryProvider(labels.getString("Application_Name"))

    val fileSep = "_"
    val filename = "take$fileSep$sLang$fileSep$sAnth$fileSep$sBook$fileSep$sChap$fileSep$sChunk$fileSep$take.wav"

    val separator = FileSystems.getDefault().separator
    val path = "$sLang$separator$sAnth$separator$sBook$separator$sChap$separator$sChunk"

    fun createFile(): File{
        val pathDir = directoryProvider.getUserDataDirectory("", true);
        val takeFile = File(pathDir + path, filename)
        //create the audio take file with the given name
        takeFile.createNewFile()
        return takeFile
    }
}