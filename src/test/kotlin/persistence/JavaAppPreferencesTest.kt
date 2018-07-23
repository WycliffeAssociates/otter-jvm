package persistence

import org.junit.After
import org.junit.Assert
import org.junit.Test
import java.util.prefs.Preferences

class JavaAppPreferencesTest() {

    @Test
    fun testIfPutGetCurrentUserHashPutsAndGetsCorrectInfo() {
        val input = 5
        val expected = input

        val appPreferences = JavaAppPreferences()
        appPreferences.setCurrentUserId(input)
        val result = appPreferences.getCurrentUserId()
        Assert.assertEquals(expected, result)
    }

    @Test
    fun testIfGetCurrentUserHashWhenNoExistingHashReturnsZero() {
        val expected = 0

        val appPreferences = JavaAppPreferences()
        val result = appPreferences.getCurrentUserId()

        Assert.assertEquals(expected, result)
    }

    @After
    fun tearDown() {
        Preferences.userNodeForPackage(JavaAppPreferences::class.java).clear()
    }
}