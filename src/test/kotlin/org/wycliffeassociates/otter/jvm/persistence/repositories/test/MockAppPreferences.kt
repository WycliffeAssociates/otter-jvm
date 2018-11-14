package org.wycliffeassociates.otter.jvm.persistence.repositories.test

import org.wycliffeassociates.otter.common.persistence.IAppPreferences

class MockAppPreferences : IAppPreferences {
    var editorId: Int? = null
    var recorderId: Int? = null
    override fun getCurrentUserId(): Int? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setCurrentUserId(userId: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAppInitialized(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setAppInitialized(initialized: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getEditorPluginId(): Int? = editorId

    override fun setEditorPluginId(id: Int) {
        editorId = id
    }

    override fun getRecorderPluginId(): Int? = recorderId

    override fun setRecorderPluginId(id: Int) {
        recorderId = id
    }

}