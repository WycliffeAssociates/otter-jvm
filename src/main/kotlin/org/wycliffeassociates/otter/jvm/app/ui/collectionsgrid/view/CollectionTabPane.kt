package org.wycliffeassociates.otter.jvm.app.ui.collectionsgrid.view

import javafx.beans.property.Property
import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.scene.control.TabPane
import org.wycliffeassociates.otter.common.data.model.ResourceMetadata
import org.wycliffeassociates.otter.jvm.app.theme.AppStyles
import org.wycliffeassociates.otter.jvm.app.ui.mainscreen.view.MainScreenStyles
import tornadofx.*

class CollectionTabPane(
        private val sourceItems: ObservableList<ResourceMetadata>,
        private val resourceProperty: Property<ResourceMetadata>
) : TabPane() {

    enum class ResourceIdentifier(val text: String) {
        // TODO
        SCRIPTURE("ulb"),
        TN("tn")
    }

    enum class LabelText(val text: String) {
        // TODO: i18n
        SCRIPTURE("Scripture"),
        TRANSLATION_NOTES("tN")
    }

    init {
        initTabs()
        sourceItems.onChange {
            initTabs()
        }
        tabs.firstOrNull()?.select()
    }

    private fun initTabs() {
        tabs.clear()
        sourceItems.forEach {
            createTab(it)
        }
    }

    private fun createTab(resource: ResourceMetadata) {
        val labelGraphicClass = when (resource.identifier) {
            ResourceIdentifier.SCRIPTURE.text ->
                Triple(LabelText.SCRIPTURE.text, null, MainScreenStyles.scripture)
            ResourceIdentifier.TN.text ->
                Triple(LabelText.TRANSLATION_NOTES.text, AppStyles.tNGraphic(), MainScreenStyles.translationNotes)
            else -> Triple(resource.identifier, null, null) // Unexpected identifier
        }
        tab(labelGraphicClass.first) {
            labelGraphicClass.second?.let { graphic = it }
            labelGraphicClass.third?.let { addClass(it) }
            setOnSelectionChanged {
                if (isSelected) {
                    resourceProperty.value = resource
                }
            }
        }
    }
}

fun EventTarget.collectionTabPane(
        items: ObservableList<ResourceMetadata>,
        resourceProperty: Property<ResourceMetadata>,
        op: CollectionTabPane.() -> Unit = {}
) = CollectionTabPane(items, resourceProperty).attachTo(this, op)
