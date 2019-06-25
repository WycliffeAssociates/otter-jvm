package org.wycliffeassociates.otter.jvm.app.ui.workbook.viewmodel

import org.junit.Assert
import org.junit.Test
import tornadofx.ViewModel

class WorkbookViewModelTest: ViewModel() {
    val workbookViewModel: WorkbookViewModel by inject()

    @Test
    fun activeResourceSlugPropertyOnChange_setsFileNamerBuilderRcSlug() {
        Assert.assertEquals(workbookViewModel.fileNamerBuilder.rcSlug, null)

        val rcSlug = "tn"
        workbookViewModel.activeResourceSlugProperty.set(rcSlug)
        Assert.assertEquals(workbookViewModel.fileNamerBuilder.rcSlug, rcSlug)

        workbookViewModel.activeResourceSlugProperty.set(null)
        Assert.assertEquals(workbookViewModel.fileNamerBuilder.rcSlug, null)
    }
}