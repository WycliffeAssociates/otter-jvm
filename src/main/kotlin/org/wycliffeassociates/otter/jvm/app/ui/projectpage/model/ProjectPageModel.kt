package org.wycliffeassociates.otter.jvm.app.ui.projectpage.model

import io.reactivex.rxjavafx.schedulers.JavaFxScheduler
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
                .subscribe { projectRoots ->
                    // TODO: Use localized resource
                    projectTitle = projectRoots.first().titleKey
                }
    }

    fun selectChildCollection(child: Collection) {

    }
}
