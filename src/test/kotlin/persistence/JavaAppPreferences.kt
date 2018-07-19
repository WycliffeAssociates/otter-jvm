package persistence

import org.junit.After
import org.junit.Assert
import org.junit.Test
import java.util.prefs.Preferences

/*class TestJavaAppPreferences() {

    @Test
    fun testIfPutGetCurrentUserHashPutsAndGetsCorrectInfo() {
        val input = "Edvin's-Amazing-Hash-0a98fe"
        val expected = input

        val appPreferences = JavaAppPreferences()
        appPreferences.putCurrentUserHash(input)
        val result = appPreferences.getCurrentUserHash()
        Assert.assertEquals(expected, result)
    }

    @Test
    fun testIfGetCurrentUserHashWhenNoExistingHashReturnsEmptyString() {
        val expected = ""

        val appPreferences = JavaAppPreferences()
        val result = appPreferences.getCurrentUserHash()

        Assert.assertEquals(expected, result)
    }

    @After
    fun tearDown() {
        Preferences.userNodeForPackage(JavaAppPreferences::class.java).clear()
    }
}*/