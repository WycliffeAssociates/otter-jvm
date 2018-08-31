package org.wycliffeassociates.otter.jvm.device.audioplugin.parser

import org.junit.Test
import org.wycliffeassociates.otter.common.data.audioplugin.AudioPluginData
import org.wycliffeassociates.otter.jvm.device.audioplugin.AudioPlugin
import java.io.File

class AudioPluginTest {

    @Test
    fun testIfLaunchExecutableFinishes() {
        // Create the AudioPlugin
        // TODO: needs to be written to pass on all platforms
        val audioPluginData = AudioPluginData(
                0,
                "Test",
                "0.0.1",
                true,
                true,
                "echo",
                listOf("myarg"),
                File("somefile.yaml")
        )
        val audioPlugin = AudioPlugin(audioPluginData)

        // Run the test and check if process finished
        audioPlugin.launch(File("message")).test().assertComplete()
    }
}