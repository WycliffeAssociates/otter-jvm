package org.wycliffeassociates.otter.jvm.device.audioplugin

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.Single
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.wycliffeassociates.otter.common.data.audioplugin.AudioPluginData
import org.wycliffeassociates.otter.common.persistence.repositories.IAudioPluginRepository
import org.wycliffeassociates.otter.jvm.device.audioplugin.parser.ParsedAudioPluginDataMapper
import java.io.File

@RunWith(PowerMockRunner::class)
@PrepareForTest(ParsedAudioPluginDataMapper::class) // prep the mapper since it uses the static System class
class AudioPluginRegistrarTest {
    private val importedPlugins = mutableListOf<AudioPluginData>()
    private var mockAudioPluginRepo: IAudioPluginRepository = mock {
        on { insert(any()) }.then { call ->
            Single.fromCallable {
                importedPlugins.add(call.getArgument(0)) // extract the audio plugin data
                return@fromCallable 0 // dummy id
            }
        }
    }

    // UUT
    private val audioRegistrar: AudioPluginRegistrar = AudioPluginRegistrar(mockAudioPluginRepo)

    val PLUGIN_PLATFORM_TABLE = listOf(
            mapOf(
                    "os.name" to "Mac OS X",
                    "expectedExecutable" to "/Applications/Audacity.app/Contents/MacOS/Audacity",
                    "expectedOcenExecutable" to "/Applications/ocenaudio.app/Contents/MacOS/ocenaudio"
            ),
            mapOf(
                    "os.name" to "Windows 10",
                    "expectedExecutable" to "C:\\Program Files (x86)\\Audacity\\audacity.exe",
                    "expectedOcenExecutable" to "C:\\Program Files (x86)\\ocenaudio\\ocenaudio.exe"
            ),
            mapOf(
                    "os.name" to "Linux",
                    "expectedExecutable" to "audacity",
                    "expectedOcenExecutable" to "ocenaudio"
            )
    )

    @Before
    fun setup() {
        // Mock the System
        PowerMockito.mockStatic(System::class.java)
    }

    @Test
    fun testImportOnePlugin() {
        // Try to import from test resources YAML file
        val testInputFile = File(ClassLoader.getSystemResource("audacity.yaml").toURI().path)

        for (testCase in PLUGIN_PLATFORM_TABLE) {
            // Configure System os.name property
            Mockito
                    .`when`(System.getProperty("os.name"))
                    .thenReturn(testCase["os.name"])

            // Expected plugin data from resources/audacity.yaml
            val expectedPluginData = AudioPluginData(
                    0,
                    "Audacity",
                    "1.0.3",
                    true,
                    true,
                    testCase["expectedExecutable"] ?: "",
                    listOf("arg1", "-t arg3"),
                    testInputFile
            )

            // Import the plugin file
            importedPlugins.clear()
            audioRegistrar
                    .import(testInputFile)
                    .blockingAwait()

            // Check the output (in class variable)
            Assert.assertEquals(listOf(expectedPluginData), importedPlugins)
        }
    }

    @Test
    fun testImportAllPlugins() {
        // Try to import all plugins from test resources folder
        val testPluginDir = File(
                ClassLoader.getSystemResource("").toURI().path
                        // Have to replace since default folder is "classes"
                        .replace("classes", "resources")
        )

        // Check if we got all the stuff we needed
        for (testCase in PLUGIN_PLATFORM_TABLE) {
            // Configure System os.name property
            Mockito
                    .`when`(System.getProperty("os.name"))
                    .thenReturn(testCase["os.name"])

            // Expected plugin data from resources/*
            val expectedAudacityPluginData = AudioPluginData(
                    0,
                    "Audacity",
                    "1.0.3",
                    true,
                    true,
                    testCase["expectedExecutable"] ?: "",
                    listOf("arg1", "-t arg3"),
                    testPluginDir.resolve("audacity.yaml")
            )
            val expectedOcenPluginData = AudioPluginData(
                    0,
                    "ocenaudio",
                    "0.0.1",
                    true,
                    false,
                    testCase["expectedOcenExecutable"] ?: "",
                    emptyList(),
                    testPluginDir.resolve("ocenaudio.yaml")
            )
            val expectedPluginData = listOf(
                    expectedAudacityPluginData,
                    expectedOcenPluginData
            )

            // Import the plugin file
            importedPlugins.clear()
            audioRegistrar
                    .importAll(testPluginDir)
                    .blockingAwait()

            // Check the output (in class variable)
            // Sort to make sure the order is the same
            Assert.assertEquals(expectedPluginData.sortedBy { it.name }, importedPlugins.sortedBy { it.name })
        }
    }

    @Test
    fun testExceptionThrownIfInvalidYAML() {
        // Try to import from test resources YAML file
        val testInputFile = File(
                ClassLoader.getSystemResource("invalid-yaml${File.separator}audacity-invalid.yaml").toURI().path
        )

        // Configure System os.name property
        Mockito
                .`when`(System.getProperty("os.name"))
                .thenReturn(PLUGIN_PLATFORM_TABLE[0]["os.name"])

        // Import the plugin file
        // Should throw exception
        try {
            audioRegistrar
                    .import(testInputFile)
                    .blockingAwait()
            Assert.fail() // should have thrown exception
        } catch (e: RuntimeException) {
            // UnrecognizedPropertyException from Jackson ends up throwing a RuntimeException
        }
    }

    @Test
    fun testRuntimeExceptionThrownIfDirectoryPassedForFile() {
        // Try to import directory instead of file
        val testInputFile = File(
                ClassLoader
                        .getSystemResource("invalid-yaml")
                        .toURI()
                        .path
        )

        // Configure System os.name property
        Mockito
                .`when`(System.getProperty("os.name"))
                .thenReturn(PLUGIN_PLATFORM_TABLE[0]["os.name"])

        // Import the plugin file
        // Should throw exception
        try {
            audioRegistrar
                    .import(testInputFile)
                    .blockingAwait()
            Assert.fail() // should have thrown exception
        } catch (e: RuntimeException) {
            // FileNotFoundException from ends up throwing a RuntimeException
        }
    }

    @Test
    fun testIllegalStateExceptionThrownIfFilePassedForDirectory() {
        // Try to import directory instead of file
        val testInputFile = File(
                ClassLoader.getSystemResource("audacity.yaml").toURI().path
        )

        // Configure System os.name property
        Mockito
                .`when`(System.getProperty("os.name"))
                .thenReturn(PLUGIN_PLATFORM_TABLE[0]["os.name"])

        // Import the plugin directory
        // Should throw exception
        try {
            audioRegistrar
                    .importAll(testInputFile)
                    .blockingAwait()
            Assert.fail() // should have thrown exception
        } catch (e: IllegalStateException) {
            // IllegalStateException occurs when trying to listFiles in a file instead of directory
        }
    }
}