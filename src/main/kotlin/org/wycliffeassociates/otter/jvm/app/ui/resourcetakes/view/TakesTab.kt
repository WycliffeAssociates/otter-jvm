package org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.view

import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import org.wycliffeassociates.otter.common.data.workbook.Take
import org.wycliffeassociates.otter.common.domain.content.Recordable
import java.util.concurrent.Callable
import tornadofx.*

class TakesTab(
    labelProperty: StringProperty,
    // tabPaneProperty gets set to null every time the tab gets removed from the tab pane so we need to cache it
    private val parent: TabPane,
    onTabSelect: (Recordable) -> Unit
): Tab() {
    private val recordableProperty = SimpleObjectProperty<Recordable?>()
    var recordable by recordableProperty

    private val takesList = FXCollections.observableArrayList<Take>()

    init {
        textProperty().bind(labelProperty)

        TakesTabContent(takesList).apply {
            formattedTextProperty.bind(getFormattedTextBinding())
            this@TakesTab.content = this.root
        }

        selectedProperty().onChange { selected ->
            if (selected) {
                recordable?.let { onTabSelect(it) }
            }
        }

        recordableProperty.onChange { item ->
            item?.let {
                loadTakes(it, takesList)
                checkAndAddTab()
            } ?: removeTab()
        }
    }

    private fun getFormattedTextBinding() = Bindings.createStringBinding(Callable { getFormattedText() }, recordableProperty)
    private fun getFormattedText(): String? = recordable?.textItem?.text

    private fun checkAndAddTab() {
        if (!parent.tabs.contains(this)) {
            parent.tabs.add(this)
        }
    }

    private fun removeTab() {
        parent.tabs.remove(this)
    }

    private fun loadTakes(recordable: Recordable, list: ObservableList<Take>) {
        list.clear()
        recordable.audio.takes
            .filter { it.deletedTimestamp.value == null }
            .subscribe {
                list.add(it)
                list.removeOnDeleted(it)
            }
    }

    private fun ObservableList<Take>.removeOnDeleted(take: Take) {
        take.deletedTimestamp.subscribe { dateHolder ->
            if (dateHolder.value != null) {
                this.remove(take)
            }
        }
    }
}