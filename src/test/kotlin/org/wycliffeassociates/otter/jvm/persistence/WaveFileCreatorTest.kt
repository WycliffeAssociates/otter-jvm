package org.wycliffeassociates.otter.jvm.persistence

import org.junit.After
import org.junit.Assert
import org.junit.Test
import java.io.File

class WaveFileCreatorTest {
    // UUT
    private val waveFileCreator = WaveFileCreator()

    private val testFile = File("./unit-test.wav")

    private val waveHeader = listOf(
            0x52, 0x49, 0x46, 0x46,
            36, 0x00, 0x00, 0x00,
            0x57, 0x41, 0x56, 0x45,
            0x66, 0x6d, 0x74, 0x20,
            0x10, 0x00, 0x00, 0x00,
            0x01, 0x00,
            0x01, 0x00,
            0x44, 0xAC, 0x00, 0x00,
            0x88, 0x58, 0x01, 0x00,
            0x02, 0x00,
            0x10, 0x00,
            0x64, 0x61, 0x74, 0x61,
            0x00, 0x00, 0x00, 0x00
    ).map { it.toByte() }


    @Test
    fun shouldCreateWaveFile() {
        Assert.assertTrue(!testFile.exists())
        waveFileCreator.createEmpty(testFile)
        Assert.assertTrue(testFile.exists())
        if (testFile.exists()) {
            Assert.assertEquals(waveHeader, testFile.readBytes().asList())
        }
        // Check WAV file header
    }

    @After
    fun tearDown() {
        if (testFile.exists()) testFile.delete()
    }
}