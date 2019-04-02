package org.wycliffeassociates.otter.jvm.resourcecardpage.view

import tornadofx.View
import tornadofx.Workspace
import tornadofx.removeFromParent
import tornadofx.vbox

class ResourcesView : View() {

    override val root = vbox {}

    var activeFragment: Workspace = Workspace()

    init {
        activeFragment.header.removeFromParent()

        activeFragment.dock<ResourceListFragment>()
        add(activeFragment)
    }
}