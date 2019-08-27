package org.wycliffeassociates.otter.jvm.app.ui.chromeablestage.model

import javafx.scene.control.Tab
import org.wycliffeassociates.otter.jvm.app.ui.takemanagement.view.RecordScriptureFragment
import tornadofx.*

class RecordChunkTabGroup : TabGroup() {
    override fun activate() {
        tabPane.tabs.add(
            Tab().apply {
                add(RecordScriptureFragment().root)
            }
        )
    }
}