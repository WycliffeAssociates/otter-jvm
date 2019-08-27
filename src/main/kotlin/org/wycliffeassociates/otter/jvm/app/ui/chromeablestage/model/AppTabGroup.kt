package org.wycliffeassociates.otter.jvm.app.ui.chromeablestage.model

import javafx.scene.control.Tab
import org.wycliffeassociates.otter.jvm.app.ui.projectgrid.view.ProjectGridFragment
import tornadofx.*

class AppTabGroup : TabGroup() {
    override fun activate() {
        workbookViewModel.activeWorkbookProperty.set(null)

        tabPane.tabs.add(
            Tab().apply {
                add(ProjectGridFragment().root)
            }
        )
    }
}
