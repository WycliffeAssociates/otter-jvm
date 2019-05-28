package org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.view

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
            "Snippet",
            viewModel.titleText,
            viewModel.titleTakes
        ) { viewModel.setTitleAsActiveTextAudioPair() }
    }

    private fun addBodyTabIfNecessary() {
        viewModel.bodyTextAudioPair?.let {
            addTab(
                "Note",
                viewModel.bodyText,
                viewModel.bodyTakes
            ) { viewModel.setBodyAsActiveTextAudioPair() }
        }
    }

    private fun addTab(
        title: String,
        formattedText: String,
        takesList: ObservableList<Take>,
        onTabSelect: () -> Unit
    ) {
        val tab = Tab().apply {
            text = title
            content = ResourceTakesTabFragment(formattedText, takesList).root
            selectedProperty().onChange {
                if (it) {
                    onTabSelect()
                }
            }
        }
        tabPane.tabs.add(tab)
    }
}