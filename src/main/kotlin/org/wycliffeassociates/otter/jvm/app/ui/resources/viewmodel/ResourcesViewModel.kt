package org.wycliffeassociates.otter.jvm.app.ui.resources.viewmodel

import org.wycliffeassociates.otter.common.data.workbook.*
import org.wycliffeassociates.otter.common.utils.mapNotNull
import org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.viewmodel.RecordResourceViewModel
import org.wycliffeassociates.otter.jvm.app.ui.workbook.viewmodel.WorkbookViewModel
import org.wycliffeassociates.otter.jvm.app.widgets.resourcecard.model.ResourceGroupCardItemList
import org.wycliffeassociates.otter.jvm.app.widgets.resourcecard.model.resourceGroupCardItem
import tornadofx.*

class ResourcesViewModel : ViewModel() {
    private val recordResourceViewModel: RecordResourceViewModel by inject()
    private val workbookViewModel: WorkbookViewModel by inject()

    val resourceGroups: ResourceGroupCardItemList = ResourceGroupCardItemList()

    fun loadResourceGroups() {
        val chapter = workbookViewModel.chapter
        chapter
            .children
            .startWith(chapter)
            .mapNotNull {
                resourceGroupCardItem(it, workbookViewModel.resourceSlug, onSelect = this::navigateToTakesPage)
            }
            .buffer(2) // Buffering by 2 prevents the list UI from jumping while groups are loading
            .subscribe {
                resourceGroups.addAll(it)
            }
    }

    private fun navigateToTakesPage(bookElement: BookElement, resource: Resource) {
        // TODO use navigator to navigate to takes page
        checkIfChunkAndSet(bookElement)
        recordResourceViewModel.setRecordableListItems(
            listOfNotNull(resource.title, resource.body)
        )
    }

    private fun checkIfChunkAndSet(bookElement: BookElement) {
        when (bookElement) {
            is Chunk -> workbookViewModel.activeChunkProperty.set(bookElement)
            else -> workbookViewModel.activeChunkProperty.set(null)
        }
    }
}