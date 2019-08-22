package org.wycliffeassociates.otter.jvm.app.ui.chromeablestage.model

import org.wycliffeassociates.otter.common.navigation.ITabGroupBuilder

class TabGroupBuilder : ITabGroupBuilder {
    override fun createAppTabGroup() = AppTabGroup()
    override fun createWorkbookTabGroup() = WorkbookTabGroup()
    override fun createActionTabGroup() = ActionTabGroup()
    override fun createResourceComponentTabGroup() = ResourceComponentTabGroup()
}