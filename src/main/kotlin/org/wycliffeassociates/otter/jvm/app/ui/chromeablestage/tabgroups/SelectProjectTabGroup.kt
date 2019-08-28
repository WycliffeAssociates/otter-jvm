package org.wycliffeassociates.otter.jvm.app.ui.chromeablestage.tabgroups

import javafx.scene.control.Tab
import org.wycliffeassociates.otter.jvm.app.ui.projectgrid.view.ProjectGridFragment
import tornadofx.*

class SelectProjectTabGroup : TabGroup() {
    override fun activate() {
        workbookViewModel.activeWorkbookProperty.set(null)

        tabPane.tabs.add(
            Tab().apply {
                add(ProjectGridFragment().root)
            }
        )
    }
}
