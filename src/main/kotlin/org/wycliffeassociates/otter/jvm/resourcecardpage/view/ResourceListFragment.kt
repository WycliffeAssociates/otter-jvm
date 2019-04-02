package org.wycliffeassociates.otter.jvm.resourcecardpage.view

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.ReplayRelay
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.layout.Priority
import org.wycliffeassociates.otter.common.data.rxmodel.*
import org.wycliffeassociates.otter.jvm.app.ui.mainscreen.view.MainScreenStyles
import org.wycliffeassociates.otter.jvm.resourcecardpage.styles.ResourceListStyles
import tornadofx.*

class ResourceListFragment : Fragment() {

    var resourceGroups: ObservableList<ResourceGroup> = FXCollections.observableList(mutableListOf())

    init {
        importStylesheet<MainScreenStyles>()
        importStylesheet<ResourceListStyles>()

        val groups: MutableList<ResourceGroup> = mutableListOf()
        for (i in 0..175) {
            groups.add(ResourceGroup(createListOfResources(i, (i % 5) + 1), "Verse $i Resources"))
        }
        resourceGroups.addAll(groups)
    }

    // Temporary function to create dummy data
    private fun createListOfResources(verseNum: Int, n: Int): List<Resource> {
        val list: MutableList<Resource> = mutableListOf()
        for (i in 1..n) {
            list.add(resource(verseNum, i))
        }
        return list
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

    // Temporary function to create dummy data
    private fun resource(verseNum: Int, resourceNum: Int): Resource {
        val titleAudio = AssociatedAudio(ReplayRelay.create(), BehaviorRelay.create())
        val bodyAudio = AssociatedAudio(ReplayRelay.create(), BehaviorRelay.create())
        titleAudio.selected.accept(1)
        bodyAudio.selected.accept(1)
        return Resource(
                0,
                0,
                TextItem("Verse $verseNum, Title $resourceNum", MimeType.MARKDOWN),
                null,
                titleAudio,
                bodyAudio
        )
    }
}