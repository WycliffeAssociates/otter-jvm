package org.wycliffeassociates.otter.jvm.app.ui.chromeablestage.model

import org.wycliffeassociates.otter.common.navigation.ITabGroupBuilder

class TabGroupBuilder : ITabGroupBuilder {
    override fun createAppTabGroup() = AppTabGroup()
    override fun createChooseChapterTabGroup() = ChooseChapterTabGroup()
    override fun createChooseRecordableTabGroup() = ChooseRecordableTabGroup()
    override fun createRecordChunkTabGroup() = RecordChunkTabGroup()
    override fun createResourceComponentTabGroup() = ResourceComponentTabGroup()
}