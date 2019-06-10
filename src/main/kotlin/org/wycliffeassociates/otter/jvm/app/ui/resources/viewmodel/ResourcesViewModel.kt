package org.wycliffeassociates.otter.jvm.app.ui.resources.viewmodel

import javafx.application.Platform
import org.wycliffeassociates.otter.common.data.workbook.*
import org.wycliffeassociates.otter.common.domain.content.Recordable
import org.wycliffeassociates.otter.common.utils.mapNotNull
import org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.viewmodel.TakesViewModel
import org.wycliffeassociates.otter.jvm.app.ui.workbook.viewmodel.WorkbookViewModel
import org.wycliffeassociates.otter.jvm.app.widgets.resourcecard.model.ResourceGroupCardItemList
import org.wycliffeassociates.otter.jvm.app.widgets.resourcecard.model.resourceGroupCardItem
import org.wycliffeassociates.otter.jvm.resourcestestapp.app.ResourceTakesApp
import org.wycliffeassociates.otter.jvm.resourcestestapp.view.ResourcesView
import tornadofx.*

class ResourcesViewModel : ViewModel() {

    private val resourcesView: ResourcesView by inject()

    private val takesViewModel: TakesViewModel by inject()
    private val workbookViewModel: WorkbookViewModel by inject()

    val resourceGroups: ResourceGroupCardItemList = ResourceGroupCardItemList(mutableListOf())

    fun loadResourceGroups() {
        val chapter = workbookViewModel.chapter
        val resourceSlug = workbookViewModel.resourceSlug
        if (chapter == null) {
            throw Exception("Loading resource groups: chapter should not be null")
        }
        chapter
            .children
            .startWith(chapter)
            .mapNotNull { resourceGroupCardItem(it, resourceSlug, onSelect = this::navigateToTakesPage) }
            .buffer(2) // Buffering by 2 prevents the list UI from jumping while groups are loading
            .subscribe {
                Platform.runLater {
                    resourceGroups.addAll(it)
                }
            }
    }

    private fun navigateToTakesPage(resource: Resource) {
        // TODO use navigator
        resourcesView.dockTakesView()
        takesViewModel.setRecordableListItems(buildRecordableItems(resource))
    }

    private fun buildRecordableItems(resource: Resource): List<Recordable> {
        // TODO: Use active chunk
        val titleRecordable = Recordable.build(ResourceTakesApp.createTestChunk(), resource.title)
        val bodyRecordable = resource.body?.let {
            Recordable.build(ResourceTakesApp.createTestChunk(), it)
        }

//        val titleRecordable = Recordable.build(ResourceTakesApp.createTestChunk(), resource.title, ResourceTakesApp.createRandomizedAssociatedAudio())
//        val bodyRecordable = resource.body?.let {
//            Recordable.build(ResourceTakesApp.createTestChunk(), it, ResourceTakesApp.createRandomizedAssociatedAudio())
//        }

//        val titleRecordable = ResourceTakesApp.titleRecordable
//        val bodyRecordable = ResourceTakesApp.bodyRecordable

        return listOfNotNull(titleRecordable, bodyRecordable)
    }
}