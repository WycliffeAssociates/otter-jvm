package org.wycliffeassociates.otter.jvm.resourcestestapp.viewmodel

import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.common.data.workbook.*
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import org.wycliffeassociates.otter.jvm.app.widgets.resourcecard.model.ResourceCardItem
import org.wycliffeassociates.otter.jvm.app.widgets.resourcecard.model.ResourceGroupCardItem
import tornadofx.ViewModel

class ResourcesViewModel : ViewModel() {

    // All of this is just to get what we will already have
    private val injector: Injector by inject()
    private val collectionRepository = injector.collectionRepo
    private val workbookRepository = injector.workbookRepository
    var workbook: Workbook

    var chapter: Chapter
    var resourceSlug = "tn"
    var resourceGroups: ObservableList<ResourceGroupCardItem> = FXCollections.observableArrayList()

    private fun resourceGroupCardItem(element: BookElement, slug: String): ResourceGroupCardItem? {

        val resourceGroup = element.resources.firstOrNull {
            it.info.slug == slug
        }
        return resourceGroup?.let { rg ->
            ResourceGroupCardItem(
                element.title,
                rg.resources.map {
                    ResourceCardItem(it) {
                        navigateToTakesPage(it)
                    }
                }
            )
        }
    }

    private fun navigateToTakesPage(resource: Resource) {
        // TODO use navigator
        println(resource.title)
    }

    init {
        val targetProject = collectionRepository.getRootProjects().blockingGet().first()
        val sourceProject = collectionRepository.getSource(targetProject).blockingGet()
        workbook = workbookRepository.get(sourceProject, targetProject)
        chapter = workbook.source.chapters.blockingFirst()

        chapter.chunks.map {
            resourceGroupCardItem(it, resourceSlug)
        }.startWith(
            resourceGroupCardItem(chapter, resourceSlug)
        ).buffer(2).subscribe { // Buffering by 2 prevents the list UI from jumping while groups are loading
            Platform.runLater {
                resourceGroups.addAll(it)
            }
        }
    }
}