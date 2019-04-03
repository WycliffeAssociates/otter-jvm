package org.wycliffeassociates.otter.jvm.resourcestestapp.view

import javafx.scene.layout.Priority
import org.wycliffeassociates.otter.jvm.app.ui.mainscreen.view.MainScreenStyles
import org.wycliffeassociates.otter.jvm.app.widgets.resourcecard.view.resourcegroupcard
import org.wycliffeassociates.otter.jvm.resourcestestapp.styles.ResourceListStyles
import org.wycliffeassociates.otter.jvm.resourcestestapp.viewmodel.ResourcesViewModel
import tornadofx.*

class ResourceListFragment : Fragment() {

    val viewModel: ResourcesViewModel by inject()

    init {
        importStylesheet<MainScreenStyles>()
        importStylesheet<ResourceListStyles>()
    }

    override val root = vbox {

        addClass(MainScreenStyles.main)

        add(progressbanner{})

        listview(viewModel.resourceGroups) {
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