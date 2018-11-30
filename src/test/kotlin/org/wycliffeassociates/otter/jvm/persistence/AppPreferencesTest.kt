package org.wycliffeassociates.otter.jvm.persistence

import org.junit.After
import org.junit.Assert
import org.junit.Test
import java.util.prefs.Preferences

class AppPreferencesTest {
    val appPreferences = AppPreferences

    @Test
    fun shouldSetAndGetUserId() {
        val input = 5
        val expected = 5

        appPreferences.setCurrentUserId(input)
        val result = appPreferences.currentUserId()
        Assert.assertEquals(expected, result)
    }

    @Test
    fun shouldReturnNullIfNoUserId() {
        val expected = null
        val result = appPreferences.currentUserId()
        Assert.assertEquals(expected, result)
    }

    @Test
    fun shouldSetAndGetAppInit() {
        val input = true
        val expected = true
        appPreferences.setAppInitialized(input)
        val result = appPreferences.appInitialized()
        Assert.assertEquals(expected, result)
    }

    @Test
    fun shouldReturnFalseIfNoInit() {
        val expected = false
        val result = appPreferences.appInitialized()
        Assert.assertEquals(expected, result)
    }

    @Test
    fun shouldSetAndGetEditorId() {
        val input = 1
        val expected = 1
        appPreferences.setEditorPluginId(input)
        val result = appPreferences.editorPluginId()
        Assert.assertEquals(expected, result)
    }

    @Test
    fun shouldReturnNullIfNoEditorId() {
        val expected = null
        val result = appPreferences.editorPluginId()
        Assert.assertEquals(expected, result)
    }

    @Test
    fun shouldSetAndGetRecorderId() {
        val input = 1
        val expected = 1
        appPreferences.setRecorderPluginId(input)
        val result = appPreferences.recorderPluginId()
        Assert.assertEquals(expected, result)
    }

    @Test
    fun shouldReturnNullIfNoRecorderId() {
        val expected = null
        val result = appPreferences.recorderPluginId()
        Assert.assertEquals(expected, result)
    }

    @After
    fun tearDown() {
        Preferences.userNodeForPackage(AppPreferences::class.java).clear()
    }
}