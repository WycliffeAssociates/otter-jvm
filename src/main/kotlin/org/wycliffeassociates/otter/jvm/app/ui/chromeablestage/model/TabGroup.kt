package org.wycliffeassociates.otter.jvm.app.ui.chromeablestage.model

import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import org.wycliffeassociates.otter.common.navigation.ITabGroup
import org.wycliffeassociates.otter.jvm.app.ui.chromeablestage.view.ChromeableStage
import org.wycliffeassociates.otter.jvm.app.ui.workbook.viewmodel.WorkbookViewModel
import tornadofx.Component

abstract class TabGroup : Component(), ITabGroup {
    abstract val tabs: List<Tab>

    protected val workbookViewModel: WorkbookViewModel by inject()
    private val chromeableStage: ChromeableStage by inject()
    protected val tabPane: TabPane = chromeableStage.root
}