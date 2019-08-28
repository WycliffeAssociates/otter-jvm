package org.wycliffeassociates.otter.jvm.app.ui.chromeablestage.model

import javafx.scene.control.Tab
import org.wycliffeassociates.otter.jvm.app.ui.cardgrid.view.CardGridFragment
import org.wycliffeassociates.otter.jvm.app.ui.resources.view.ResourceListFragment
import tornadofx.*

class ChooseRecordableTabGroup : TabGroup() {
    override fun activate() {
        workbookViewModel.activeChunkProperty.set(null)

        when(workbookViewModel.activeResourceInfo.type) {
            "book" -> createChooseChunkTab()
            "help" -> createChooseResourceTab()
        }
    }

    private fun createChooseChunkTab() {
        tabPane.tabs.add(
            Tab().apply {
                add(CardGridFragment().root)
            }
        )
    }

    private fun createChooseResourceTab() {
        tabPane.tabs.add(
            Tab().apply {
                add(ResourceListFragment().root)
            }
        )
    }
}
