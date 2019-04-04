package org.wycliffeassociates.otter.jvm.resourcestestapp.viewmodel

import javafx.beans.property.SimpleBooleanProperty
import org.wycliffeassociates.otter.common.data.workbook.Resource

class ResourceCardViewModel(private val resource: Resource) {

    val titleTakeSelected = SimpleBooleanProperty(false)
    val bodyTakeSelected = SimpleBooleanProperty(false)

    init {
        resource.titleAudio.selected.subscribe {
            titleTakeSelected.set(it != null)
        }

        resource.bodyAudio?.selected?.subscribe {
            bodyTakeSelected.set(it != null)
        }
    }

    fun navigateToTakesPage() {
        // TODO
    }
}