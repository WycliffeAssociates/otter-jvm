package org.wycliffeassociates.otter.jvm.app.ui.collectionsgrid.view

import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.scene.control.TabPane
import org.wycliffeassociates.otter.jvm.app.theme.AppStyles
import tornadofx.*

class CollectionTabPane(private val sourceItems: ObservableList<String>) : TabPane() {

    enum class ResourceIdentifier(val text: String) {
        SCRIPTURE("scripture"), // Not technically an identifier but we use this to populate the first tab
        TN("tn")
    }

    enum class LabelText(val text: String) {
        SCRIPTURE("Scripture"),
        TRANSLATION_NOTES("tN")
    }

    init {
        initTabs()
        sourceItems.onChange {
            initTabs()
        }
    }

    private fun initTabs() {
        tabs.clear()
        createTab(ResourceIdentifier.SCRIPTURE.text)
        sourceItems.forEach {
            createTab(it)
        }
    }

    private fun createTab(identifier: String) {
        val labelGraphicClass = when (identifier) {
            ResourceIdentifier.SCRIPTURE.text ->
                Triple(LabelText.SCRIPTURE.text, null, CollectionGridStyles.scripture)
            ResourceIdentifier.TN.text ->
                Triple(LabelText.TRANSLATION_NOTES.text, AppStyles.tNGraphic(), CollectionGridStyles.translationNotes)
            else -> Triple(identifier, null, CssRule("", "")) // Unexpected identifier
        }
        tab(labelGraphicClass.first) {
            labelGraphicClass.second?.let { graphic = it }
            addClass(labelGraphicClass.third)
        }
    }
}

fun EventTarget.collectionTabPane(
        items: ObservableList<String>,
        op: CollectionTabPane.() -> Unit = {}
) = CollectionTabPane(items).attachTo(this, op)
