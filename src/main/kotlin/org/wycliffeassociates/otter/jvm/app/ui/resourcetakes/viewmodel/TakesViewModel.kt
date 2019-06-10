package org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.viewmodel

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.common.domain.content.Recordable
import tornadofx.*

class TakesViewModel : ViewModel() {
    val recordableList: ObservableList<Recordable> = FXCollections.observableArrayList()

    val activeRecordableItemProperty = SimpleObjectProperty<Recordable>()
    var activeRecordableItem by activeRecordableItemProperty

    // TODO: Observe workbookviewmodel's activeResource property and change these
    val titleTabLabelProperty = SimpleStringProperty(messages["snippet"])
    var titleTabLabel by titleTabLabelProperty

    val bodyTabLabelProperty = SimpleStringProperty(messages["note"])
    var bodyTabLabel by bodyTabLabelProperty

    fun onTabSelect(recordable: Recordable) {
        activeRecordableItem = recordable
    }

    fun setRecordableListItems(items: List<Recordable>) {
        if (!recordableList.containsAll(items))
            recordableList.setAll(items)
    }
}