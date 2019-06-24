package org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.viewmodel

import com.github.thomasnield.rxkotlinfx.observeOnFx
import io.reactivex.schedulers.Schedulers
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.common.data.model.ContentType
import org.wycliffeassociates.otter.common.domain.content.Recordable
import org.wycliffeassociates.otter.jvm.app.ui.workbook.viewmodel.WorkbookViewModel
import org.wycliffeassociates.otter.common.domain.content.RecordTake
import org.wycliffeassociates.otter.common.domain.plugins.LaunchPlugin
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import org.wycliffeassociates.otter.jvm.persistence.WaveFileCreator
import org.wycliffeassociates.otter.jvm.utils.getNotNull
import java.util.EnumMap
import tornadofx.*

class TakesViewModel : ViewModel() {
    private val injector: Injector by inject()
    private val pluginRepository = injector.pluginRepository

    private val workbookViewModel: WorkbookViewModel by inject()

    val recordableList: ObservableList<Recordable> = FXCollections.observableArrayList()

    val activeRecordableProperty = SimpleObjectProperty<Recordable>()
    var activeRecordable by activeRecordableProperty

    private val recordTake = RecordTake(
        WaveFileCreator(),
        LaunchPlugin(pluginRepository)
    )

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
        recordTake.record(
            activeRecordable.audio,
            workbookViewModel.projectAudioDirectory
        ).observeOnFx()
            // Subscribing on an I/O thread is not completely necessary but it is is safer
        .subscribeOn(Schedulers.io())
        .subscribe()
    }
}