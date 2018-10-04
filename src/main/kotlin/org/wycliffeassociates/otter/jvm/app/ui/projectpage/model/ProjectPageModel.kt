package org.wycliffeassociates.otter.jvm.app.ui.projectpage.model

import io.reactivex.rxjavafx.schedulers.JavaFxScheduler
import io.reactivex.schedulers.Schedulers
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.common.data.model.Chunk
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import org.wycliffeassociates.otter.jvm.app.ui.projectpage.view.ChapterContext
import org.wycliffeassociates.otter.jvm.persistence.repositories.ProjectRepository
import tornadofx.getProperty
import tornadofx.property

class ProjectPageModel {
    // setup model with fx properties
    var projectTitle: String by property()
    val projectTitleProperty = getProperty(ProjectPageModel::projectTitle)

    // List of collection children (i.e. the chapters) to display in the list
    var children: ObservableList<Collection> = FXCollections.observableList(mutableListOf())

    // List of chunks to display on the screen
    var chunks: ObservableList<Chunk> = FXCollections.observableList(mutableListOf())

    // What record/review/edit context are we in?
    var context: ChapterContext by property(ChapterContext.RECORD)
    var contextProperty = getProperty(ProjectPageModel::context)

    init {
        // TODO: Get from use case
        Injector
                .projectRepo
                .getAllRoot()
                .observeOn(JavaFxScheduler.platform())
                .map {
                    val project = it.first()
                    // TODO: Use localized resource
                    projectTitle = project.titleKey
                    project
                }
                .observeOn(Schedulers.io())
                .flatMap {
                    Injector.projectRepo.getChildren(it)
                }
                .observeOn(JavaFxScheduler.platform())
                .doOnSuccess {
                    // Now we have the children of the project collection
                    children.clear()
                    children.addAll(it)
                }
                .subscribe()
    }

    fun selectChildCollection(child: Collection) {
        println("Selecting child ${child.titleKey}")
    }

    fun doChunkContextualAction(chunk: Chunk) {
        println("Doing action $context for ${chunk.labelKey}")
    }
}
