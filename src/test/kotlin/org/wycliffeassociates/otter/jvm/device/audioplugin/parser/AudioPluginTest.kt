//package org.wycliffeassociates.otter.jvm.device.audioplugin.parser
//
//import org.junit.Assert
//import org.junit.Test
//import org.wycliffeassociates.otter.jvm.device.audioplugin.AudioPlugin
//import java.io.File
//
//class AudioPluginTest {
//
//    @Test
//    fun testIfLaunchAbleToRunExecutable() {
//        // Create the AudioPlugin
//        // TODO: needs to be written to pass on all platforms
//        val audioPlugin = AudioPlugin(
//                0,
//                "Test",
//                "0.0.1",
//                true,
//                true,
//                File("ls"),
//                listOf("-a"),
//                File("somefile.yaml")
//        )
//
//        // Run the test and check if process finished
//        audioPlugin.launch(File("*")).test().assertComplete()
//    }
//
//    @Test
//    fun testDeletePluginFile() {
//        // Create a file to delete
//        val pluginFile = File(listOf(".","test.yaml").joinToString(File.separator))
//        pluginFile.createNewFile()
//
//        // Create the AudioPlugin
//        val audioPlugin = AudioPlugin(
//                0,
//                "Test",
//                "0.0.1",
//                true,
//                true,
//                File(""),
//                emptyList(),
//                pluginFile
//        )
//
//        // Run the test
//        audioPlugin.delete().blockingAwait()
//
//        // Check result
//        try {
//            Assert.assertFalse(pluginFile.exists())
//        } finally {
//            // Clean up, if test failed
//            if (pluginFile.exists()) pluginFile.delete()
//        }
//    }
//}