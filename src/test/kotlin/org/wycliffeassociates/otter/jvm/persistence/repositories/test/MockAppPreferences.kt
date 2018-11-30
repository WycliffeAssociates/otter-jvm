package org.wycliffeassociates.otter.jvm.persistence.repositories.test

import org.wycliffeassociates.otter.common.persistence.IAppPreferences

class MockAppPreferences : IAppPreferences {
    var editorId: Int? = null
    var recorderId: Int? = null
    override fun currentUserId(): Int? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setCurrentUserId(userId: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun appInitialized(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setAppInitialized(initialized: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun editorPluginId(): Int? = editorId

    override fun setEditorPluginId(id: Int) {
        editorId = id
    }

    override fun recorderPluginId(): Int? = recorderId

    override fun setRecorderPluginId(id: Int) {
        recorderId = id
    }

}