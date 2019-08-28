package org.wycliffeassociates.otter.jvm.app.ui.chromeablestage.tabgroups

import javafx.scene.control.TabPane
import org.wycliffeassociates.otter.common.navigation.ITabGroup
import org.wycliffeassociates.otter.jvm.app.ui.chromeablestage.ChromeableStage
import org.wycliffeassociates.otter.jvm.app.ui.workbook.viewmodel.WorkbookViewModel
import tornadofx.Component

abstract class TabGroup : Component(), ITabGroup {
    protected val workbookViewModel: WorkbookViewModel by inject()
    private val chromeableStage: ChromeableStage by inject()
    protected val tabPane: TabPane = chromeableStage.root

    override fun deactivate() {
        tabPane.tabs.clear()
    }
}