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
import kotlin.math.min

class TakesTab(
    labelProperty: StringProperty,
    // tabPaneProperty gets set to null every time the tab gets removed from the tab pane so we need to cache it
    private val parent: TabPane,
    val sort: Int,
    private val onTabSelect: (Recordable) -> Unit
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
                checkAndAddSelf()
            } ?: removeSelf()
        }
    }

    private fun getFormattedTextBinding() = Bindings.createStringBinding(Callable { getFormattedText() }, recordableProperty)
    private fun getFormattedText(): String? = recordable?.textItem?.text

    private fun checkAndAddSelf() {
        if (!parent.tabs.contains(this)) {
            addSelfToParent()
        }
    }

    private fun removeSelf() {
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

    private fun addSelfToParent() {
        parent.tabs.add(min(sort, parent.tabs.size), this)
        if (parent.tabs.size == 1) {
            selectTab()
        }
    }

    private fun selectTab() {
        select()
        recordable?.let { onTabSelect(it) }
    }
}