package org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.view

import com.jakewharton.rxrelay2.ReplayRelay
import com.sun.javafx.application.PlatformImpl
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TabPane
import org.junit.Assert
import org.junit.Test
import org.wycliffeassociates.otter.common.data.model.ContentType
import org.wycliffeassociates.otter.common.data.model.MimeType
import org.wycliffeassociates.otter.common.data.workbook.AssociatedAudio
import org.wycliffeassociates.otter.common.data.workbook.Resource
import org.wycliffeassociates.otter.common.data.workbook.Take
import org.wycliffeassociates.otter.common.data.workbook.TextItem
import org.wycliffeassociates.otter.common.domain.content.Recordable
import org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.viewmodel.RecordResourceViewModel
import org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.viewmodel.RecordableTabViewModel
import org.wycliffeassociates.otter.jvm.app.ui.workbook.viewmodel.WorkbookViewModel
import tornadofx.*

class RecordableTabTest : ViewModel() {
    private val recordResourceViewModel: RecordResourceViewModel by inject()
    private val workbookViewModel: WorkbookViewModel by inject()
    private val parent: TabPane
    private val recordableTab: RecordableTab
    private val testRecordable: Recordable

    init {
        PlatformImpl.startup {}
        workbookViewModel.activeResourceSlugProperty.set("tn")

        parent = TabPane()

        testRecordable = Resource.Component(
            0,
            TextItem("dummy text", MimeType.MARKDOWN),
            AssociatedAudio(ReplayRelay.create<Take>()),
            ContentType.TITLE
        )

        val tabViewModel = RecordableTabViewModel(SimpleStringProperty("dummy label")).apply {
            recordable = testRecordable
        }

        recordableTab = RecordableTab(
            tabViewModel,
            parent,
            0,
            recordResourceViewModel::onTabSelect
        )

        parent.tabs.add(recordableTab)
    }

    // TODO: This is Integration
    @Test
    fun testOnSelect_setsActiveRecordable() {
        recordableTab.select()

        Assert.assertEquals(testRecordable, recordResourceViewModel.activeRecordable)
    }
}