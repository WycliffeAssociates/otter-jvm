package app.filesystem

class AudioStore (private val filename: String) {

    private val directoryProvider = DirectoryProvider("translationRecorder")

    fun storeFile(fileString: String) : Boolean{
        val pathString = directoryProvider.getUserDataDirectory() + fileString
        return true

    }

}