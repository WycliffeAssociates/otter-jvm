package org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.viewmodel

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.common.data.model.ContentType
import org.wycliffeassociates.otter.common.domain.content.Recordable
import org.wycliffeassociates.otter.jvm.app.ui.workbook.viewmodel.WorkbookViewModel
import org.wycliffeassociates.otter.jvm.app.ui.takemanagement.viewmodel.TakeManagementViewModel
import org.wycliffeassociates.otter.jvm.utils.getNotNull
import java.util.EnumMap
import tornadofx.*

class RecordResourceViewModel : ViewModel() {
    private val workbookViewModel: WorkbookViewModel by inject()
    private val takeManagementViewModel: TakeManagementViewModel by inject()

    val recordableList: ObservableList<Recordable> = FXCollections.observableArrayList()

    val activeRecordableProperty = SimpleObjectProperty<Recordable>()
    var activeRecordable by activeRecordableProperty

    class ContentTypeToLabelPropertyMap(map: Map<ContentType, SimpleStringProperty>):
        EnumMap<ContentType, SimpleStringProperty>(map)
    val contentTypeToLabelPropertyMap = ContentTypeToLabelPropertyMap(
        hashMapOf(
            ContentType.TITLE to SimpleStringProperty(),
            ContentType.BODY to SimpleStringProperty()
        )
    )

    init {
        setTabLabels(workbookViewModel.resourceSlug)
        workbookViewModel.activeResourceSlugProperty.onChange {
            setTabLabels(it)
        }
    }

    private fun setTabLabels(resourceSlug: String?) {
        when(resourceSlug) {
            "tn" -> {
                contentTypeToLabelPropertyMap.getNotNull(ContentType.TITLE).set(messages["snippet"])
                contentTypeToLabelPropertyMap.getNotNull(ContentType.BODY).set(messages["note"])
            }
        }
    }

    fun onTabSelect(recordable: Recordable) {
        activeRecordable = recordable
    }

    fun setRecordableListItems(items: List<Recordable>) {
        if (!recordableList.containsAll(items))
            recordableList.setAll(items)
    }

    fun newTakeAction() {
        takeManagementViewModel.recordNewTake(activeRecordable)
    }
}