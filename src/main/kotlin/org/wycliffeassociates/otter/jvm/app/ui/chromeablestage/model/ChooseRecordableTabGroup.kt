package org.wycliffeassociates.otter.jvm.app.ui.chromeablestage.model

import javafx.scene.control.Tab
import org.wycliffeassociates.otter.jvm.app.ui.cardgrid.view.CardGridFragment
import org.wycliffeassociates.otter.jvm.app.ui.projectwizard.view.SlugsEnum
import org.wycliffeassociates.otter.jvm.app.ui.resources.view.ResourceListFragment
import tornadofx.*

class ChooseRecordableTabGroup : TabGroup() {
    override fun activate() {
        when(workbookViewModel.resourceSlug) {
            SlugsEnum.ULB.slug -> createChooseChunkTab()
            SlugsEnum.TN.slug -> createChooseResourceTab()
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
