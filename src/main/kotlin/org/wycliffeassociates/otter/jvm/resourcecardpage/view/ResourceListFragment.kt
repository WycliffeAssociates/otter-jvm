package org.wycliffeassociates.otter.jvm.resourcecardpage.view

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.layout.Priority
import org.wycliffeassociates.otter.common.data.rxmodel.*
import org.wycliffeassociates.otter.jvm.app.ui.mainscreen.view.MainScreenStyles
import org.wycliffeassociates.otter.jvm.app.widgets.resourcecard.resourcegroupcard
import org.wycliffeassociates.otter.jvm.resourcecardpage.app.ResourceCardApp
import org.wycliffeassociates.otter.jvm.resourcecardpage.styles.ResourceListStyles
import tornadofx.*

class ResourceListFragment : Fragment() {

    var resourceGroups: ObservableList<ResourceGroup> = FXCollections.observableList(mutableListOf())

    init {
        importStylesheet<MainScreenStyles>()
        importStylesheet<ResourceListStyles>()

        val groups = ResourceCardApp.createResourceGroups()
        resourceGroups.addAll(groups)
    }

    override val root = vbox {

        addClass(MainScreenStyles.main)

        add(progressbanner{})

        listview(resourceGroups) {
            vgrow = Priority.ALWAYS // This needs to be here
            cellFormat {
                graphic = resourcegroupcard(it)
            }
            isFocusTraversable = false
            addClass(ResourceListStyles.resourceGroupList)
        }
    }
}