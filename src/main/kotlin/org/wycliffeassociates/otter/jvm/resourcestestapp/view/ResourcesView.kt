package org.wycliffeassociates.otter.jvm.resourcestestapp.view

import org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.view.ResourceTakesView
import tornadofx.View
import tornadofx.Workspace
import tornadofx.removeFromParent
import tornadofx.vbox

class ResourcesView : View() {

    override val root = vbox {}

    var activeFragment: Workspace = Workspace()

    init {
        activeFragment.header.removeFromParent()

        activeFragment.dock<ResourceTakesView>()
        add(activeFragment)
    }
}