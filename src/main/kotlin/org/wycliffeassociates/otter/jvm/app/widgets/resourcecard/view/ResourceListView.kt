package org.wycliffeassociates.otter.jvm.app.widgets.resourcecard.view

import javafx.collections.ObservableList
import javafx.scene.control.ListView
import javafx.scene.layout.Priority
import org.wycliffeassociates.otter.jvm.app.widgets.resourcecard.model.ResourceGroupCardItem
import org.wycliffeassociates.otter.jvm.app.widgets.resourcecard.styles.ResourceListStyles
import tornadofx.*

class ResourceListView(items: ObservableList<ResourceGroupCardItem>): ListView<ResourceGroupCardItem>(items) {
    init {
        vgrow = Priority.ALWAYS // This needs to be here
        cellFormat {
            graphic = cache(it.title) {
                resourcegroupcard(it)
            }
        }
        isFocusTraversable = false
        addClass(ResourceListStyles.resourceGroupList)
    }
}