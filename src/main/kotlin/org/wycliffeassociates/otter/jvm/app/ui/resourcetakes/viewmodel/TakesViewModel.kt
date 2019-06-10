package org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.viewmodel

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.common.domain.content.Recordable
import org.wycliffeassociates.otter.jvm.app.ui.workbook.viewmodel.WorkbookViewModel
import tornadofx.*

class TakesViewModel : ViewModel() {
    val recordableList: ObservableList<Recordable> = FXCollections.observableArrayList()

    private val workbookViewModel: WorkbookViewModel by inject()

    val activeRecordableItemProperty = SimpleObjectProperty<Recordable>()
    var activeRecordableItem by activeRecordableItemProperty

    val titleTabLabelProperty = SimpleStringProperty()
    var titleTabLabel by titleTabLabelProperty

    val bodyTabLabelProperty = SimpleStringProperty()
    var bodyTabLabel by bodyTabLabelProperty

    init {
        setTabLabels(workbookViewModel.resourceSlug)
        workbookViewModel.activeResourceSlugProperty.onChange {
            setTabLabels(it)
        }
    }
    
    private fun setTabLabels(resourceSlug: String?) {
        when(resourceSlug) {
            "tn" -> {
                titleTabLabel = messages["snippet"]
                bodyTabLabel = messages["note"]
            }
        }
    }

    fun onTabSelect(recordable: Recordable) {
        activeRecordableItem = recordable
    }

    fun setRecordableListItems(items: List<Recordable>) {
        if (!recordableList.containsAll(items))
            recordableList.setAll(items)
    }
}