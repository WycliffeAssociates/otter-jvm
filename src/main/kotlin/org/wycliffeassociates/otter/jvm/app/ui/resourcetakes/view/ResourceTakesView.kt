package org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.view

import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.scene.control.Tab
import org.wycliffeassociates.controls.ChromeableTabPane
import org.wycliffeassociates.otter.common.data.workbook.Take
import org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.viewmodel.TakesViewModel
import tornadofx.*

class ResourceTakesView : Fragment() {

    private val viewModel: TakesViewModel by inject()

    private val tabPane = ChromeableTabPane()

    init {
        importStylesheet<ResourceTakesStyles>()

        addSnippetTab()
        addBodyTabIfNecessary()
    }

    override val root = tabPane

    private fun addSnippetTab() {
        addTab(
            messages["snippet"],
            viewModel.titleTextProperty,
            viewModel.titleTakes
        ) { viewModel.setTitleAsActiveRecordableItem() }
    }

    private fun addBodyTabIfNecessary() {
        viewModel.bodyRecordableItem?.let {
            addTab(
                messages["note"],
                viewModel.bodyTextProperty,
                viewModel.bodyTakes
            ) { viewModel.setBodyAsActiveRecordableItem() }
        }
    }

    private fun addTab(
        title: String,
        formattedTextProperty: StringProperty,
        takesList: ObservableList<Take>,
        onTabSelect: () -> Unit
    ) {
        val tab = Tab().apply {
            text = title
            content = ResourceTakesTabFragment(formattedTextProperty, takesList).root
            selectedProperty().onChange {
                if (it) {
                    onTabSelect()
                }
            }
        }
        tabPane.tabs.add(tab)
    }
}