package org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.viewmodel

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.common.data.workbook.Take
import org.wycliffeassociates.otter.common.domain.content.RecordableItem
import org.wycliffeassociates.otter.jvm.resourcestestapp.app.ResourceTakesApp
import tornadofx.*

class TakesViewModel : ViewModel() {
    val titleRecordableItemProperty = SimpleObjectProperty<RecordableItem>()
    var titleRecordableItem by titleRecordableItemProperty

    val bodyRecordableItemProperty = SimpleObjectProperty<RecordableItem?>()
    var bodyRecordableItem by bodyRecordableItemProperty

    val activeRecordableItemProperty = SimpleObjectProperty<RecordableItem>(titleRecordableItem)
    var activeRecordableItem by activeRecordableItemProperty

    val titleTextProperty = SimpleStringProperty("[title]")
    val bodyTextProperty = SimpleStringProperty("[body]")

    val titleTakes: ObservableList<Take> = FXCollections.observableArrayList()
    val bodyTakes: ObservableList<Take> = FXCollections.observableArrayList()

    init {
        titleRecordableItemProperty.onChange {
            loadTakes(titleRecordableItem, titleTakes)
            titleRecordableItem.textItem?.text?.let { titleText ->
                titleTextProperty.set(titleText)
            }
        }

        bodyRecordableItemProperty.onChange { bodyRecordableItem ->
            bodyRecordableItem?.let {
                loadTakes(it, bodyTakes)
                it.textItem?.text.let { bodyText ->
                    bodyTextProperty.set(bodyText)
                }
            }
        }

//        loadTestRecordableItems()
    }

    private fun loadTestRecordableItems() {
        titleRecordableItem = ResourceTakesApp.titleRecordableItem
        bodyRecordableItem = ResourceTakesApp.bodyRecordableItem
    }

    fun loadTakes(recordableItem: RecordableItem, list: ObservableList<Take>) {
        list.clear()
        recordableItem.audio.takes
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

    fun setTitleAsActiveRecordableItem() {
        activeRecordableItem = titleRecordableItem
    }

    fun setBodyAsActiveRecordableItem() {
        activeRecordableItem = bodyRecordableItem
    }
}