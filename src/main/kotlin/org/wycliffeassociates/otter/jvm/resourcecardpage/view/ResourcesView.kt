package org.wycliffeassociates.otter.jvm.resourcecardpage.view

import org.wycliffeassociates.otter.jvm.resourcecardpage.viewmodel.ResourcesViewModel
import tornadofx.View
import tornadofx.Workspace
import tornadofx.removeFromParent
import tornadofx.vbox

class ResourcesView : View() {

    private val viewModel: ResourcesViewModel by inject()

    override val root = vbox {}

    var activeFragment: Workspace = Workspace()

    init {
        activeFragment.header.removeFromParent()

        activeFragment.dock<ResourcePageFragment>()
        add(activeFragment)
    }
}