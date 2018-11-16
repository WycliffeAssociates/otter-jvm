import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.common.data.model.ResourceMetadata
import org.wycliffeassociates.otter.jvm.persistence.DirectoryProvider
import java.io.File
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files
import java.time.LocalDate

@RunWith(PowerMockRunner::class)
@PrepareForTest(DirectoryProvider::class)
class DirectoryProviderTest {
    private var mockFileSystem: FileSystem = mock()

    private val OS_TABLE = listOf(
            "Mac OS X",
            "Linux",
            "Windows 10"
    )

    @Before
    fun setup() {
        // setup up the mock of System
        PowerMockito.mockStatic(System::class.java)
        PowerMockito.mockStatic(FileSystems::class.java)
        whenever(FileSystems.getDefault()).thenReturn(mockFileSystem)
        PowerMockito.mockStatic(Files::class.java)
    }

    @Test
    fun shouldReturnPlatformSpecificAppDataDirectory() {
        for (testOs in OS_TABLE) {
            configureMocksForAppData(testOs)

            // get the result
            val fileResult = DirectoryProvider("translationRecorder")
                    .getAppDataDirectory("")

            // assert
            assertVerbose(expectedAppDataDirectory("", testOs), fileResult, testOs)
            tearDown(fileResult)
        }
    }

    @Test
    fun shouldReturnPlatformSpecificUserDataDirectory() {
        for (testOs in OS_TABLE) {
            configureMocksForUserData(testOs)

            // get the result
            val fileResult = DirectoryProvider("translationRecorder")
                    .getUserDataDirectory("")

            // assert
            assertVerbose(expectedUserDataDirectory("", testOs), fileResult, testOs)
            tearDown(fileResult)
        }
    }

    @Test
    fun shouldReturnPlatformSpecificResourceContainerDirectory() {
        for (testOs in OS_TABLE) {
            configureMocksForAppData(testOs)

            // get the result
            val fileResult = DirectoryProvider("translationRecorder").resourceContainerDirectory

            // assert
            assertVerbose(expectedAppDataDirectory("rc", testOs), fileResult, testOs)
            tearDown(fileResult)
        }
    }

    @Test
    fun shouldReturnPlatformSpecificAudioPluginDirectory() {
        for (testOs in OS_TABLE) {
            configureMocksForAppData(testOs)

            // get the result
            val fileResult = DirectoryProvider("translationRecorder").audioPluginDirectory

            // assert
            assertVerbose(expectedAppDataDirectory("plugins", testOs), fileResult, testOs)
            tearDown(fileResult)
        }
    }

    @Test
    fun shouldReturnPlatformSpecificUserProfileImageDirectory() {
        for (testOs in OS_TABLE) {
            configureMocksForAppData(testOs)

            // get the result
            val fileResult = DirectoryProvider("translationRecorder").userProfileImageDirectory

            // assert
            assertVerbose(expectedAppDataDirectory("users${separator(testOs)}images", testOs), fileResult, testOs)
            tearDown(fileResult)
        }
    }

    @Test
    fun shouldReturnPlatformSpecificUserProfileAudioDirectory() {
        for (testOs in OS_TABLE) {
            configureMocksForAppData(testOs)

            // get the result
            val fileResult = DirectoryProvider("translationRecorder").userProfileAudioDirectory

            // assert
            assertVerbose(expectedAppDataDirectory("users${separator(testOs)}audio", testOs), fileResult, testOs)
            tearDown(fileResult)
        }
    }

    @Test
    fun shouldReturnPlatformSpecificProjectDirectory() {
        for (testOs in OS_TABLE) {
            configureMocksForUserData(testOs)

            val book = Collection(
                    1,
                    "gen",
                    "project",
                    "Genesis",
                    ResourceMetadata(
                            "rc0.2",
                            "Bible Create",
                            "The ULB translation.",
                            "text/usfm",
                            "ulb",
                            LocalDate.of(2000, 1, 1),
                            Language(
                                    "en",
                                    "English",
                                    "English",
                                    "ltr",
                                    true
                            ),
                            LocalDate.of(2018, 3, 2),
                            "Bible Publisher",
                            "Bible",
                            "book",
                            "Unlocked Literal Bible",
                            "3",
                            File(".")
                    )
            )
            val chapterDirName = "50"

            val fileResult = DirectoryProvider("translationRecorder")
                    .getProjectAudioDirectory(book, chapterDirName)

            val expectedAppendedPath = listOf(
                    book.resourceContainer!!.language.slug,
                    book.resourceContainer!!.identifier,
                    book.slug,
                    chapterDirName
            ).joinToString(separator(testOs))
            assertVerbose(expectedUserDataDirectory(expectedAppendedPath,testOs), fileResult, testOs)
            tearDown(fileResult)
        }
    }

    @Test
    fun shouldReturnPlatformSpecificProjectDirectoryWhenMetadataNull() {
        for (testOs in OS_TABLE) {
            configureMocksForUserData(testOs)

            val book = Collection(
                    1,
                    "gen",
                    "project",
                    "Genesis",
                    null
            )
            val chapterDirName = "50"

            val fileResult = DirectoryProvider("translationRecorder")
                    .getProjectAudioDirectory(book, chapterDirName)

            val expectedAppendedPath = listOf(
                    "no_language",
                    "no_rc",
                    book.slug,
                    chapterDirName
            ).joinToString(separator(testOs))
            assertVerbose(expectedUserDataDirectory(expectedAppendedPath,testOs), fileResult, testOs)
            tearDown(fileResult)
        }
    }

    private fun configureMocksForAppData(os: String) {
        // configure for OS responses
        whenever(System.getProperty("os.name")).thenReturn(os)
        whenever(mockFileSystem.separator).thenReturn(separator(os))
        when (os) {
            "Windows 10" -> whenever(System.getenv("APPDATA")).thenReturn(appDataDirectory(os))
            else -> whenever(System.getProperty("user.home")).thenReturn(homeDirectory(os))
        }
    }

    private fun configureMocksForUserData(os: String) {
        // configure for OS responses
        whenever(System.getProperty("os.name")).thenReturn(os)
        whenever(mockFileSystem.separator).thenReturn(separator(os))
        whenever(System.getProperty("user.home")).thenReturn(homeDirectory(os))
    }

    private fun assertVerbose(expectedPath: String, actual: File, os: String) {
        try {
            assertEquals(File(expectedPath), actual)
        } catch (e: AssertionError) {
            println("Input OS: ${os}")
            println("Expected: ${expectedPath}")
            println("Result:   ${actual}")
            throw e
        }
    }

    private fun tearDown(result: File) {
        if (result.exists()) result.delete()
    }

    private fun expectedAppDataDirectory(appendedPath: String, os: String): String = when (os) {
        "Mac OS X" -> "/Users/edvin/Library/Application Support/translationRecorder${ if (appendedPath.isNotEmpty()) "/" else "" }"
        "Linux" -> "/home/edvin/.config/translationRecorder${ if (appendedPath.isNotEmpty()) "/" else "" }"
        "Windows 10" -> "C:\\Users\\Edvin\\AppData\\Roaming\\translationRecorder${ if (appendedPath.isNotEmpty()) "\\" else "" }"
        else -> ""
    }.plus(appendedPath)

    private fun expectedUserDataDirectory(appendedPath: String, os: String): String = when (os) {
        "Mac OS X" -> "/Users/edvin/translationRecorder${ if (appendedPath.isNotEmpty()) "/" else "" }"
        "Linux" -> "/home/edvin/translationRecorder${ if (appendedPath.isNotEmpty()) "/" else "" }"
        "Windows 10" -> "C:\\Users\\Edvin\\translationRecorder${ if (appendedPath.isNotEmpty()) "\\" else "" }"
        else -> ""
    }.plus(appendedPath)

    private fun homeDirectory(os: String): String = when (os) {
        "Mac OS X" -> "/Users/edvin"
        "Linux" -> "/home/edvin"
        "Windows 10" -> "C:\\Users\\Edvin"
        else -> ""
    }

    private fun appDataDirectory(os: String): String = when (os) {
        "Windows 10" -> "C:\\Users\\Edvin\\AppData\\Roaming"
        else -> ""
    }

    private fun separator(os: String): String = when (os) {
        "Windows 10" -> "\\"
        else -> "/"
    }

}