package org.wycliffeassociates.otter.jvm.device.audioplugin.parser

import audioplugin.yamlparser.YAMLAudioPlugin
import audioplugin.yamlparser.YAMLExecutable
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.modules.junit4.PowerMockRunner
import org.wycliffeassociates.otter.jvm.device.audioplugin.AudioPlugin
import java.io.File

@RunWith(PowerMockRunner::class)
class AudioPluginMapperTest {
    init {
        PowerMockito.mockStatic(System::class.java)
    }

    val PLUGIN_PLATFORM_TABLE = listOf(
            mapOf(
                    "os.name" to "Mac OS X",
                    "expectedExecutable" to "/Applications/Audacity.app/Contents/MacOS/Audacity"
            ),
            mapOf(
                    "os.name" to "Windows 10",
                    "expectedExecutable" to "C:\\Users\\Program Files (x86)\\Audacity\\audacity.exe"
            ),
            mapOf(
                    "os.name" to "Linux",
                    "expectedExecutable" to "audacity"
            )
    )

    @Test
    fun testCorrectPluginCreatedForEachPlatform() {
        // Create the inputs for the test
        val inputPluginFile = File("/location/of/plugin/audacity.yaml")
        val inputYamlPlugin = YAMLAudioPlugin(
                "Audacity",
                "1.0.1",
                true,
                false,
                YAMLExecutable(
                        File("/Applications/Audacity.app/Contents/MacOS/Audacity"),
                        File("C:\\Users\\Program Files (x86)\\Audacity\\audacity.exe"),
                        File("audacity")
                ),
                listOf("-t value")
        )

        // Iterate over OS tests
        for (testCase in PLUGIN_PLATFORM_TABLE) {
            // Mock the OS
            Mockito.`when`(System.getProperty("os.name")).thenReturn(testCase["os.name"])

            // Build the expected result
            val expectedAudioPlugin = AudioPlugin(
                    0,
                    inputYamlPlugin.name,
                    inputYamlPlugin.version,
                    inputYamlPlugin.canEdit,
                    inputYamlPlugin.canRecord,
                    File(testCase["expectedExecutable"]),
                    inputYamlPlugin.args,
                    inputPluginFile
            )

            // Run the mapper
            val result = AudioPluginMapper().mapToAudioPlugin(inputYamlPlugin, inputPluginFile)

            // Assert the result
            //Assert.assertTrue(expectedAudioPlugin.equals(result))
            Assert.assertEquals(expectedAudioPlugin, result)
        }
    }

    @Test
    fun testExceptionThrownWhenPlatformNotSupported() {
        // Create the inputs for the test
        val inputPluginFile = File("/location/of/plugin/audacity.yaml")
        // Null executables since the platforms are not supported
        val inputYamlPlugin = YAMLAudioPlugin(
                "Audacity",
                "1.0.1",
                true,
                false,
                YAMLExecutable(
                        null,
                        null,
                        null
                ),
                listOf("-t value")
        )

        // Iterate over OS tests
        for (testCase in PLUGIN_PLATFORM_TABLE) {
            // Mock the OS
            Mockito.`when`(System.getProperty("os.name")).thenReturn(testCase["os.name"])

            // Run the mapper
            try {
                val result = AudioPluginMapper().mapToAudioPlugin(inputYamlPlugin, inputPluginFile)
                // Exception should be thrown before this line
                Assert.fail("'${testCase["os.name"]}' case did not thrown unsupported platform exception")
            } catch (e: UnsupportedPlatformException) {
                // Everything okay
            }
        }
    }

    @Test
    fun testUnrecognizedPlatformDefaultsToLinux() {
        // Create the inputs for the test
        val inputPluginFile = File("/location/of/plugin/unrecognizedplatform.yaml")
        val inputYamlPlugin = YAMLAudioPlugin(
                "Audacity",
                "1.0.1",
                true,
                false,
                YAMLExecutable(
                        File("/Applications/Audacity.app/Contents/MacOS/Audacity"),
                        File("C:\\Users\\Program Files (x86)\\Audacity\\audacity.exe"),
                        File("audacity")
                ),
                listOf("-t value")
        )

        // Configure the static System mock
        Mockito.`when`(System.getProperty("os.name")).thenReturn("HAL/S")

        // Build the expected result
        val expectedAudioPlugin = AudioPlugin(
                0,
                inputYamlPlugin.name,
                inputYamlPlugin.version,
                inputYamlPlugin.canEdit,
                inputYamlPlugin.canRecord,
                File("audacity"),
                inputYamlPlugin.args,
                inputPluginFile
        )

        // Run the mapper
        val result = AudioPluginMapper().mapToAudioPlugin(inputYamlPlugin, inputPluginFile)

        Assert.assertEquals(expectedAudioPlugin, result)
    }
}