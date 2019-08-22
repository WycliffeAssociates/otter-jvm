package org.wycliffeassociates.otter.jvm.app.ui.chromeablestage.model

import javafx.scene.control.Tab
import org.wycliffeassociates.otter.jvm.app.ui.projectgrid.view.ProjectGridFragment
import tornadofx.*

class AppTabGroup : TabGroup() {
    override val tabs: List<Tab> = listOf(
        Tab().apply {
            add(ProjectGridFragment().root)
        }
    )

    override fun activate() {
        tabPane.tabs.add(tabs[0])
    }

    override fun deactivate() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
