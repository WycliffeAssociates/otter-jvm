package app.filesystem

import junit.framework.TestCase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Files


@RunWith(PowerMockRunner::class)
@PrepareForTest(AudioStore::class)
class TestAudioStore {
    var mockFileSystem = Mockito.mock(FileSystem::class.java)

    val USERDATA_TESTS_TABLE = listOf(
            mapOf(
                    "expected" to "/Users/edvin/translationRecorder/Projects/Project1/take12_1.wav",
                    "os" to "Mac OS X",
                    "separator" to "/",
                    "home" to "/Users/edvin"
            ),
            mapOf(
                    "expected" to "/home/edvin/translationRecorder/Projects/Project1/take1",
                    "os" to "Linux",
                    "separator" to "/",
                    "home" to "/home/edvin"
            ),
            mapOf(
                    "expected" to "C:\\Users\\Edvin\\translationRecorder\\Projects\\Project1\\take1",
                    "os" to "Windows 10",
                    "separator" to "\\",
                    "home" to "C:\\Users\\Edvin"
            )
    )

    @Before
    fun setup() {
        // setup up the mock of System
        PowerMockito.mockStatic(System::class.java)
        PowerMockito.mockStatic(FileSystems::class.java)
        BDDMockito.given(FileSystems.getDefault()).willReturn(mockFileSystem)
        PowerMockito.mockStatic(Files::class.java)
    }

    @Test
    fun testAudioFileLocation(){
        for (test in USERDATA_TESTS_TABLE){
            // configure for OS responses
            BDDMockito.given(System.getProperty("os.name")).willReturn(test["os"])
            BDDMockito.given(mockFileSystem.separator).willReturn(test["separator"])
            BDDMockito.given(System.getProperty("user.home")).willReturn(test["home"])

            // get the result
            val result = AudioStore("Project1").createTakeFile("take12_1.wav")
            val correctLocation = AudioStore("Project1").corrLocationAndName(result)

            // assert
            try {
                TestCase.assertTrue(correctLocation)
                TestCase.assertEquals(test["expected"], result.absolutePath)
            } catch (e: AssertionError) {
                println("Input OS: ${test["os"]}")
                println("Expected: ${test["expected"]}")
                println("Result:   $result")
                throw e
            }
        }
    }
}
