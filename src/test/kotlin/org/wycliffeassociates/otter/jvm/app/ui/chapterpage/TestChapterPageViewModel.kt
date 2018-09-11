package org.wycliffeassociates.otter.jvm.app.ui.chapterpage

import org.junit.Test
import org.wycliffeassociates.otter.jvm.app.ui.chapterpage.view.Contexts
import org.wycliffeassociates.otter.jvm.app.ui.chapterpage.viewModel.ChapterPageViewModel

class TestChapterPageViewModel {
    val viewModel = ChapterPageViewModel()


    @Test
    fun testChangeContext() {
        val expected = Contexts.RECORD
        viewModel.changeContext(Contexts.RECORD)
        val actual = viewModel.selectedTab
        assert(expected == actual)
    }

    @Test
    fun testInitializeChapterPage() {

    }
}