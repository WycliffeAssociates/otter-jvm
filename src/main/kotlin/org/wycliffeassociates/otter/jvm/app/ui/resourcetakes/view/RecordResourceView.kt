package org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.view

import javafx.collections.ListChangeListener
import org.wycliffeassociates.controls.ChromeableTabPane
import org.wycliffeassociates.otter.common.data.model.ContentType
import org.wycliffeassociates.otter.common.domain.content.Recordable
import org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.viewmodel.TakesViewModel
import java.util.EnumMap
import tornadofx.*
import org.wycliffeassociates.otter.jvm.utils.getNotNull

class RecordResourceView : View() {
    private val viewModel: TakesViewModel by inject()
    private val tabPane = ChromeableTabPane()

    class ContentTypeToTabMap(map: Map<ContentType, RecordableTab>): EnumMap<ContentType, RecordableTab>(map)
    private val contentTypeToTabMap = ContentTypeToTabMap(
        hashMapOf(
            ContentType.TITLE to takesTab(ContentType.TITLE, 0),
            ContentType.BODY to takesTab(ContentType.BODY, 1)
        )
    )

    private fun takesTab(contentType: ContentType, sort: Int): RecordableTab {
        val labelProp = viewModel.contentTypeToLabelPropertyMap.getNotNull(contentType)
        return RecordableTab(
            labelProp,
            tabPane,
            sort,
            viewModel::onTabSelect
        )
    }

    override val root = tabPane

    init {
        importStylesheet<RecordResourceStyles>()

        initTabs()

        viewModel.recordableList.onChange {
            updateTabs(it)
        }
    }

    private fun initTabs() {
        viewModel.recordableList.forEach {
            addRecordableToTab(it)
        }
    }

    private fun updateTabs(change: ListChangeListener.Change<out Recordable>) {
        while (change.next()) {
            change.removed.forEach { recordable ->
                removeRecordableFromTab(recordable)
            }
            change.addedSubList.forEach { recordable ->
                addRecordableToTab(recordable)
            }
        }
    }

    private fun addRecordableToTab(item: Recordable) {
        contentTypeToTabMap.getNotNull(item.contentType).recordable = item
    }

    private fun removeRecordableFromTab(item: Recordable) {
        contentTypeToTabMap.getNotNull(item.contentType).recordable = null
    }
}