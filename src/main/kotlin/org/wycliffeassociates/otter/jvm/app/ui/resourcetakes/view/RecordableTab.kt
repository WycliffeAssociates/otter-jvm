package org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.view

import io.reactivex.disposables.CompositeDisposable
import javafx.application.Platform
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

class RecordableTab(
    labelProperty: StringProperty,
    // tabPaneProperty gets set to null every time the tab gets removed from the tab pane so we need to cache it
    private val parent: TabPane,
    val sort: Int,
    private val onTabSelect: (Recordable) -> Unit
): Tab() {
    private val recordableProperty = SimpleObjectProperty<Recordable?>()
    var recordable by recordableProperty

    private val takesList = FXCollections.observableArrayList<Take>()
    private val disposables = CompositeDisposable()

    init {
        textProperty().bind(labelProperty)

        RecordableTabContent(takesList).apply {
            formattedTextProperty.bind(getFormattedTextBinding())
            this@RecordableTab.content = this.root
        }

        selectedProperty().onChange { selected ->
            if (selected) {
                recordable?.let { onTabSelect(it) }
            }
        }

        recordableProperty.onChange { item ->
            clearDisposables()
            item?.let {
                loadTakes(it, takesList)
                checkAndAddSelf()
            } ?: removeSelf()
        }
    }

    @Suppress("ProtectedInFinal", "Unused")
    protected fun finalize() {
        clearDisposables()
    }

    private fun clearDisposables() {
        disposables.clear()
    }

    private fun getFormattedTextBinding() = Bindings.createStringBinding(
        Callable { getFormattedText() },
        recordableProperty
    )

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
            .subscribe {
                Platform.runLater {
                    list.add(it)
                    list.removeOnDeleted(it)
                }
            }.let { disposables.add(it) }
    }

    private fun ObservableList<Take>.removeOnDeleted(take: Take) {
        take.deletedTimestamp.subscribe { dateHolder ->
            if (dateHolder.value != null) {
                this.remove(take)
            }
        }.let { disposables.add(it) }
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