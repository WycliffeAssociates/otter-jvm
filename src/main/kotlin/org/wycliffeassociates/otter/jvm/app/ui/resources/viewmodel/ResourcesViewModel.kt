package org.wycliffeassociates.otter.jvm.app.ui.resources.viewmodel

import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import org.wycliffeassociates.otter.common.data.workbook.*
import org.wycliffeassociates.otter.common.domain.content.Recordable
import org.wycliffeassociates.otter.common.utils.mapNotNull
import org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.viewmodel.TakesViewModel
import org.wycliffeassociates.otter.jvm.app.widgets.resourcecard.model.ResourceGroupCardItemList
import org.wycliffeassociates.otter.jvm.app.widgets.resourcecard.model.resourceGroupCardItem
import org.wycliffeassociates.otter.jvm.resourcestestapp.app.ResourceTakesApp
import org.wycliffeassociates.otter.jvm.resourcestestapp.view.ResourcesView
import tornadofx.*

class ResourcesViewModel : ViewModel() {

    val resourcesView: ResourcesView by inject()

    val takesViewModel: TakesViewModel by inject()

    val activeWorkbookProperty = SimpleObjectProperty<Workbook>()
    val workbook: Workbook
        get() = activeWorkbookProperty.value

    // TODO: Move to workbook view model
    val activeChapterProperty = SimpleObjectProperty<Chapter>()
    val chapter: Chapter
        get() = activeChapterProperty.value

    val activeResourceSlugProperty = SimpleStringProperty()
    val resourceSlug: String
        get() = activeResourceSlugProperty.value

    val resourceGroups: ResourceGroupCardItemList = ResourceGroupCardItemList(mutableListOf())

    fun loadResourceGroups() {
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