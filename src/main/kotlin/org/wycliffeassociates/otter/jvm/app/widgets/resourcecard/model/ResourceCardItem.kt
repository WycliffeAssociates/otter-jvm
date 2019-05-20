package org.wycliffeassociates.otter.jvm.app.widgets.resourcecard.model

import javafx.beans.property.DoubleProperty
import javafx.beans.property.SimpleDoubleProperty
import org.wycliffeassociates.otter.common.data.workbook.AssociatedAudio
import org.wycliffeassociates.otter.common.data.workbook.Resource
import org.commonmark.parser.Parser
import org.commonmark.renderer.text.TextContentRenderer

data class ResourceCardItem(val resource: Resource, val onSelect: () -> Unit) {
    // Want resource to be a val so we can navigate to the takes page with the resource
    val title: String = getTitleTextContent()
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

    companion object {
        val parser: Parser = Parser.builder().build()
        val renderer: TextContentRenderer = TextContentRenderer.builder().build()
    }

    private fun getTitleTextContent(): String {
        val document = parser.parse(resource.title.text)
        return renderer.render(document)
    }
}