package org.wycliffeassociates.otter.jvm.app.ui.collectionsgrid.view

import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.scene.control.TabPane
import org.wycliffeassociates.otter.jvm.app.theme.AppStyles
import tornadofx.*

class CollectionTabPane(private val sourceItems: ObservableList<String>) : TabPane() {

    init {
        initTabs()
        sourceItems.onChange {
            initTabs()
        }
    }

    private fun initTabs() {
        tabs.clear()
        createTab("bundle")
        sourceItems.forEach {
            createTab(it)
        }
    }

    private fun createTab(type: String) {
        val labelGraphicPair = when (type) {
            "bundle" -> Pair("Scripture", null) // TODO: Enums
            "help" -> Pair("tN", AppStyles.tNGraphic())
            else -> Pair("unsupported", null)   //TODO: Error
        }
        tab(labelGraphicPair.first) {
            labelGraphicPair.second?.let { graphic = it }
        }
    }
}

fun EventTarget.collectionTabPane(
        items: ObservableList<String>,
        op: CollectionTabPane.() -> Unit = {}
) = CollectionTabPane(items).attachTo(this, op)
