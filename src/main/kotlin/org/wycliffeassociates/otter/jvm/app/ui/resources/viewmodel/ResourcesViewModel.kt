package org.wycliffeassociates.otter.jvm.app.ui.resources.viewmodel

import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.common.data.workbook.*
import org.wycliffeassociates.otter.jvm.app.widgets.resourcecard.model.ResourceCardItem
import org.wycliffeassociates.otter.jvm.app.widgets.resourcecard.model.ResourceGroupCardItem
import org.wycliffeassociates.otter.jvm.app.widgets.resourcecard.model.resourceGroupCardItem
import tornadofx.*

class ResourcesViewModel : ViewModel() {
    val activeWorkbookProperty = SimpleObjectProperty<Workbook>()
    val workbook: Workbook
        get() = activeWorkbookProperty.value

    val activeChapterProperty = SimpleObjectProperty<Chapter>()
    val chapter: Chapter
        get() = activeChapterProperty.value

    val activeResourceSlugProperty = SimpleStringProperty()
    val resourceSlug: String
        get() = activeResourceSlugProperty.value

    var resourceGroups: ObservableList<ResourceGroupCardItem> = FXCollections.observableArrayList()

    fun loadResourceGroups() {
        chapter.chunks.map { chunk ->
            resourceGroupCardItem(chunk, resourceSlug) { navigateToTakesPage(it) }
        }.startWith(
            resourceGroupCardItem(chapter, resourceSlug) { navigateToTakesPage(it) }
        ).buffer(2).subscribe { // Buffering by 2 prevents the list UI from jumping while groups are loading
            Platform.runLater {
                resourceGroups.addAll(it)
            }
        }
    }

    private fun navigateToTakesPage(resource: Resource) {
        // TODO use navigator
    }
}