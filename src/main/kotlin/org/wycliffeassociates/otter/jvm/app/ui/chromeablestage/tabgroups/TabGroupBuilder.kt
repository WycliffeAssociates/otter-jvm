package org.wycliffeassociates.otter.jvm.app.ui.chromeablestage.tabgroups

import org.wycliffeassociates.otter.common.navigation.ITabGroupBuilder

class TabGroupBuilder : ITabGroupBuilder {
    override fun createSelectProjectTabGroup() = SelectProjectTabGroup()
    override fun createSelectChapterTabGroup() = SelectChapterTabGroup()
    override fun createSelectRecordableTabGroup() = SelectRecordableTabGroup()
    override fun createRecordScriptureTabGroup() = RecordScriptureTabGroup()
    override fun createRecordResourceTabGroup() = RecordResourceTabGroup()
}