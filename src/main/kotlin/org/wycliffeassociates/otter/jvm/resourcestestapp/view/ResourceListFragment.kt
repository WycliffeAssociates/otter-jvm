package org.wycliffeassociates.otter.jvm.resourcestestapp.view

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import org.wycliffeassociates.otter.common.data.workbook.Resource
import org.wycliffeassociates.otter.jvm.app.ui.mainscreen.view.MainScreenStyles
import org.wycliffeassociates.otter.jvm.app.widgets.resourcecard.model.ResourceGroupCardItem
import org.wycliffeassociates.otter.jvm.app.widgets.resourcecard.view.resourcegroupcard
import org.wycliffeassociates.otter.jvm.app.widgets.workbookheader.FilterOption
import org.wycliffeassociates.otter.jvm.app.widgets.workbookheader.workbookheader
import org.wycliffeassociates.otter.jvm.resourcestestapp.app.ResourceCardApp
import org.wycliffeassociates.otter.jvm.resourcestestapp.styles.ResourceListStyles
import tornadofx.*
import java.util.function.Predicate

class ResourceListFragment : Fragment() {

    var resourceGroups: ObservableList<ResourceGroupCardItem> = FXCollections.observableList(mutableListOf())

    init {
        importStylesheet<MainScreenStyles>()
        importStylesheet<ResourceListStyles>()

        val groups = ResourceCardApp.createResourceGroups()
        resourceGroups.addAll(groups)
    }

    override val root = vbox {

        addClass(MainScreenStyles.main)

        add(
            workbookheader<Resource> {
                labelText = "Translation Notes"
                filterOption = FilterOption(
                    "Hide Completed",
                    Predicate { true }
                    // TODO: If Resource had a isComplete() method, we could use: Predicate { it.isComplete() }
                )
                progressBarTrackFill = Color.ORANGE
            }
        )

        listview(resourceGroups) {
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
}