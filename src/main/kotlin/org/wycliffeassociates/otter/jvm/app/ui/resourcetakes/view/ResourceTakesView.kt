package org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.view

import javafx.collections.ListChangeListener
import org.wycliffeassociates.controls.ChromeableTabPane
import org.wycliffeassociates.otter.common.data.model.ContentType
import org.wycliffeassociates.otter.common.domain.content.Recordable
import org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.viewmodel.TakesViewModel
import java.util.EnumMap
import tornadofx.*

class ResourceTakesView : View() {
    private val viewModel: TakesViewModel by inject()
    private val tabPane = ChromeableTabPane()

    private val contentTypeToTabMap = EnumMap<ContentType, TakesTab>(
        hashMapOf(
            ContentType.TITLE to TakesTab(viewModel.titleTabLabelProperty, tabPane, viewModel::onTabSelect),
            ContentType.BODY to TakesTab(viewModel.bodyTabLabelProperty, tabPane, viewModel::onTabSelect)
        )
    )

    override val root = tabPane

    init {
        importStylesheet<ResourceTakesStyles>()

        addTab(ContentType.TITLE)
        addTab(ContentType.BODY)

        viewModel.recordableList.onChange {
            updateTabs(it)
        }
    }

    private fun updateTabs(change: ListChangeListener.Change<out Recordable>) {
        while (change.next()) {
            change.removed.forEach { item ->
                removeItemFromTab(item)
            }
            change.addedSubList.forEach { item ->
                addItemToTab(item)
            }
        }
    }

    private fun addItemToTab(item: Recordable) {
        contentTypeToTabMap[item.contentType]?.recordable = item
    }

    private fun removeItemFromTab(item: Recordable) {
        contentTypeToTabMap[item.contentType]?.recordable = null
    }

    private fun addTab(contentType: ContentType) {
        contentTypeToTabMap[contentType]?.let { tab ->
            tabPane.tabs.add(tab)
        }
    }
}
