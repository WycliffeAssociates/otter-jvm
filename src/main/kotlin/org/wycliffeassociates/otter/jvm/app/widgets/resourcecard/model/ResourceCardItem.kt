package org.wycliffeassociates.otter.jvm.app.widgets.resourcecard.model

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import org.wycliffeassociates.otter.common.data.workbook.AssociatedAudio
import org.wycliffeassociates.otter.common.data.workbook.Resource

data class ResourceCardItem(val resource: Resource, val onSelect: () -> Unit) {
    // Want resource to be a val so we can navigate to the takes page with the resource
    val title: String = resource.title.text
    val titleProgressProperty: DoubleProperty = resource.titleAudio.progressProperty()
    val bodyProgressProperty: DoubleProperty? = resource.bodyAudio?.progressProperty()
    val hasBodyAudio: Boolean = resource.bodyAudio != null

    private fun AssociatedAudio.progressProperty(): DoubleProperty {
        val progressProperty = SimpleDoubleProperty(0.0)
        this.selected.subscribe {
            progressProperty.set( if (it.value != null) 1.0 else 0.0)
        }
        return progressProperty
    }

    fun isCompleted(): Boolean {
        return resource.isCompleted()
    }
}