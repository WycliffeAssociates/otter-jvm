package org.wycliffeassociates.otter.jvm.resourcecardpage.view

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import org.wycliffeassociates.otter.common.data.model.AssociatedAudio
import org.wycliffeassociates.otter.common.data.model.Resource
import org.wycliffeassociates.otter.common.data.model.ResourceGroup
import org.wycliffeassociates.otter.common.data.model.TextItem
import org.wycliffeassociates.otter.jvm.app.ui.mainscreen.view.MainScreenStyles
import org.wycliffeassociates.otter.jvm.resourcecardpage.styles.ResourceCardStyles
import tornadofx.*

class ResourcePageView : View() {

    // TODO: Work on viewmodel
//    private val viewModel: ResourcePageViewModel by inject()

    var resourceGroups: ObservableList<ResourceGroup> = FXCollections.observableList(mutableListOf())

    init {
        importStylesheet<MainScreenStyles>()
        importStylesheet<ResourceCardStyles>()

        val groups: MutableList<ResourceGroup> = mutableListOf()
        for (i in 1..2) {
            groups.add(ResourceGroup(createListOfResources(3), "Verse $i Resources"))
        }
        resourceGroups.addAll(groups)
    }

    private fun createListOfResources(n: Int): List<Resource> {
        val list: MutableList<Resource> = mutableListOf()
        for (i in 1..n) {
            list.add(resource(i))
        }
        return list
    }

    override val root = vbox {

        addClass(MainScreenStyles.main)

        // TODO: This is the top progress bar banner
        hbox {
            prefHeight = 30.0
            style {
                backgroundColor += Color.YELLOW
            }
        }

        listview(resourceGroups) {
            vgrow = Priority.ALWAYS // This needs to be here
            cellFormat {
                graphic = resourcegroupcard(it)
                addClass(ResourceCardStyles.resourceGroupCard)
            }
            spacing = 15.0
            isFocusTraversable = false
            addClass(ResourceCardStyles.resourceGroupList)
        }
    }

    private fun resource(i: Int): Resource {
        return Resource(
                TextItem("type", "Title $i", ".txt"),
                null,
                0,
                AssociatedAudio(listOf(), null),
                null
        )
    }
}