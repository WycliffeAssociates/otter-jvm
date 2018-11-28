package org.wycliffeassociates.otter.jvm.persistence

import org.wycliffeassociates.otter.common.persistence.IAppPreferences
import org.wycliffeassociates.otter.jvm.persistence.database.AppDatabase
import org.wycliffeassociates.otter.jvm.persistence.entities.PreferenceEntity

// preferences object that stores user-independent preference data
class AppPreferences(database: AppDatabase) : IAppPreferences {
    private val preferenceDao = database.getPreferenceDao()
    private val CURRENT_USER_ID_KEY = "currentUserId"
    private val APP_INIT_KEY = "appInitialized"
    private val EDITOR_PLUGIN_ID_KEY = "editorPluginId"
    private val RECORDER_PLUGIN_ID_KEY = "recorderPluginId"

    private fun putInt(key: String, value: Int) {
        preferenceDao.upsert(PreferenceEntity(key, value.toString()))
    }

    private fun putBoolean(key: String, value: Boolean) {
        preferenceDao.upsert(PreferenceEntity(key, value.toString()))
    }

    private fun getInt(key: String, def: Int): Int {
        var value = def
        try {
            value = preferenceDao.fetchByKey(key).value.toInt()
        } catch (e: RuntimeException) {
            // do nothing
        }
        return value
    }

    private fun getBoolean(key: String, def: Boolean): Boolean {
        var value = def
        try {
            value = preferenceDao.fetchByKey(key).value.toBoolean()
        } catch (e: RuntimeException) {
            // do nothing
        }
        return value
    }

    override fun currentUserId(): Int? {
        val userId = getInt(CURRENT_USER_ID_KEY, -1)
        return if (userId < 0) null else userId
    }

    override fun setCurrentUserId(userId: Int) {
        putInt(CURRENT_USER_ID_KEY, userId)
    }

    override fun appInitialized(): Boolean {
        return getBoolean(APP_INIT_KEY, false)
    }

    override fun setAppInitialized(initialized: Boolean) {
        putBoolean(APP_INIT_KEY, initialized)
    }

    override fun editorPluginId(): Int? {
        val editorId = getInt(EDITOR_PLUGIN_ID_KEY, -1)
        return if (editorId < 0) null else editorId
    }

    override fun setEditorPluginId(id: Int) {
        putInt(EDITOR_PLUGIN_ID_KEY, id)
    }

    override fun recorderPluginId(): Int? {
        val recorderId = getInt(RECORDER_PLUGIN_ID_KEY, -1)
        return if (recorderId < 0) null else recorderId
    }

    override fun setRecorderPluginId(id: Int) {
        putInt(RECORDER_PLUGIN_ID_KEY, id)
    }

}