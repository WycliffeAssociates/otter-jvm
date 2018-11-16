package org.wycliffeassociates.otter.jvm.persistence.repositories

import com.nhaarman.mockitokotlin2.*
import org.junit.Assert
import org.junit.Test
import org.mockito.stubbing.Answer
import org.wycliffeassociates.otter.common.data.audioplugin.AudioPluginData
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.jvm.persistence.database.daos.AudioPluginDao
import org.wycliffeassociates.otter.jvm.persistence.repositories.test.MockAppPreferences
import org.wycliffeassociates.otter.jvm.persistence.repositories.test.MockDatabase
import java.io.File
import java.lang.RuntimeException

class AudioPluginRepositoryTest {
    private val mockDatabase = MockDatabase.database()
    private val mockPreferences = MockAppPreferences()

    // UUT
    private val pluginRepository = AudioPluginRepository(mockDatabase, mockPreferences)

    @Test
    fun shouldCRUDPluginData() {
        val pluginData = create()

        val retrieved = retrieveAll()
        Assert.assertEquals(listOf(pluginData), retrieved)

        update(pluginData)
        val retrievedUpdated = retrieveAll()
        Assert.assertEquals(listOf(pluginData), retrievedUpdated)

        delete(pluginData)
        val retrievedDeleted = retrieveAll()
        Assert.assertEquals(emptyList<AudioPluginData>(), retrievedDeleted)
    }

    @Test
    fun shouldHandleDaoExceptionInUpdate() {
        val data: AudioPluginData = mock { on { id } doReturn 0 }
        val mockExceptionDao: AudioPluginDao = mock(defaultAnswer = Answer<Any> { throw RuntimeException() })
        whenever(mockDatabase.getAudioPluginDao()).doReturn(mockExceptionDao)
        try {
            pluginRepository.update(data).blockingAwait()
        } catch (e: RuntimeException) {
            Assert.fail("Did not handle DAO exception")
        }
    }

    @Test
    fun shouldGetPlugin() {
        create()
        val retrieved = pluginRepository.getAllPlugins().blockingGet()
        Assert.assertEquals(1, retrieved.size)
    }

    @Test
    fun shouldReturnEmptyMaybeIfNoEditorData() {
        Assert.assertTrue(pluginRepository.getEditorData().isEmpty.blockingGet())
    }

    @Test
    fun shouldReturnEmptyMaybeIfNoEditor() {
        Assert.assertTrue(pluginRepository.getEditor().isEmpty.blockingGet())
    }

    @Test
    fun shouldReturnEmptyMaybeIfNoRecorderData() {
        Assert.assertTrue(pluginRepository.getRecorderData().isEmpty.blockingGet())
    }

    @Test
    fun shouldReturnEmptyMaybeIfNoRecorder() {
        Assert.assertTrue(pluginRepository.getRecorder().isEmpty.blockingGet())
    }

    @Test
    fun shouldSetAndGetEditorData() {
        val pluginData = create()

        pluginRepository.setEditorData(pluginData).blockingAwait()

        val retrievedEditor = pluginRepository.getEditorData().blockingGet()
        Assert.assertEquals(pluginData, retrievedEditor)
    }

    @Test
    fun shouldSetAndGetEditor() {
        val pluginData = create()

        pluginRepository.setEditorData(pluginData).blockingAwait()

        val retrievedEditor = pluginRepository.getEditor().blockingGet()
        Assert.assertNotNull(retrievedEditor)
    }

    @Test
    fun shouldSetAndGetRecorderData() {
        val pluginData = create()

        pluginRepository.setRecorderData(pluginData).blockingAwait()

        val retrievedRecorder = pluginRepository.getRecorderData().blockingGet()
        Assert.assertEquals(pluginData, retrievedRecorder)
    }

    @Test
    fun shouldSetAndGetRecorder() {
        val pluginData = create()

        pluginRepository.setRecorderData(pluginData).blockingAwait()

        val retrievedRecorder = pluginRepository.getRecorder().blockingGet()
        Assert.assertNotNull(retrievedRecorder)
    }

    @Test
    fun shouldSelectFirstEditorIfNoDefault() {
        val editor = create(true, false)
        create() // insert extra plugin
        pluginRepository.initSelected().blockingAwait()
        val retrievedEditor = pluginRepository.getEditorData().blockingGet()
        Assert.assertEquals(editor, retrievedEditor)
    }

    @Test
    fun shouldSelectFirstRecorderIfNoDefault() {
        val recorder = create(false, true)
        create() // insert extra plugin
        pluginRepository.initSelected().blockingAwait()
        val retrievedEditor = pluginRepository.getRecorderData().blockingGet()
        Assert.assertEquals(recorder, retrievedEditor)
    }

    @Test
    fun shouldNotOverwriteExistingDefaultOnInit() {
        val old = create()
        pluginRepository.initSelected().blockingAwait()

        create()
        pluginRepository.initSelected().blockingAwait()

        val retrievedEditor = pluginRepository.getEditorData().blockingGet()
        val retrievedRecorder = pluginRepository.getEditorData().blockingGet()
        Assert.assertEquals(old, retrievedEditor)
        Assert.assertEquals(old, retrievedRecorder)
    }

    // CRUD methods
    private fun create(edit: Boolean = true, record: Boolean = true): AudioPluginData {
        val pluginData = AudioPluginData(
                0,
                "plugin",
                "version",
                edit,
                record,
                "plugin.exe",
                listOf("args"),
                File("")
        )
        pluginData.id = pluginRepository.insert(pluginData).blockingGet()
        return pluginData
    }

    private fun retrieveAll(): List<AudioPluginData> = pluginRepository
                    .getAll()
                    .blockingGet()

    private fun update(pluginData: AudioPluginData) {
        pluginData.name = "audio"
        pluginData.version = "newest"
        pluginData.executable = "/path/to/audio"
        pluginData.canRecord = false
        pluginData.canEdit = false
        pluginData.args = listOf("--all")
        pluginRepository.update(pluginData).blockingAwait()
    }

    private fun delete(pluginData: AudioPluginData) {
        pluginRepository.delete(pluginData).blockingAwait()
    }
}