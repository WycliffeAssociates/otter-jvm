package org.wycliffeassociates.otter.jvm.app.ui.projectpage.model

import io.reactivex.Single
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler
import io.reactivex.schedulers.Schedulers
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.common.data.model.Chunk
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.domain.ProjectPageActions
import org.wycliffeassociates.otter.jvm.app.DefaultPluginPreference
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import org.wycliffeassociates.otter.jvm.app.ui.projectpage.view.ChapterContext
import org.wycliffeassociates.otter.jvm.persistence.WaveFileCreator

import tornadofx.getProperty
import tornadofx.property

class ProjectPageModel {
    var project: Collection? = null

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

    val projectPageActions = ProjectPageActions(
            Injector.directoryProvider,
            WaveFileCreator(),
            Injector.collectionRepo,
            Injector.chunkRepository,
            Injector.takeRepository
    )

    init {
        // TODO: Get from scope
        Injector
                .projectRepo
                .getAllRoot()
                .observeOn(JavaFxScheduler.platform())
                .map {
                    project = it.first()
                    project?.let {
                        // TODO: Use localized resource
                        projectTitle = it.titleKey
                    }
                    project
                }
                .observeOn(Schedulers.io())
                .flatMap {
                    projectPageActions.getChildren(it)
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
        // Remove existing chunks so the user knows they are outdated
        chunks.clear()
        projectPageActions
                .getChunks(child)
                .observeOn(JavaFxScheduler.platform())
                .subscribe { retrieved ->
                    chunks.clear() // Make sure any chunks that might have been added are removed
                    chunks.addAll(retrieved)
                }
    }

    fun checkIfChunkHasTakes(chunk: Chunk): Single<Boolean> {
        return projectPageActions
                .getTakeCount(chunk)
                .map { it > 0 }
    }

    fun doChunkContextualAction(chunk: Chunk) {
        when (context) {
            ChapterContext.RECORD -> {
                DefaultPluginPreference.defaultPlugin?.let { plugin ->
                    project?.let { project ->
                        projectPageActions
                                .createNewTake(chunk, project)
                                .flatMap { take ->
                                    projectPageActions
                                            .launchPluginForTake(take, plugin)
                                            .toSingle { take }
                                }
                                .flatMap {take ->
                                    projectPageActions.insertTake(take, chunk)
                                }
                                .subscribe()
                    }
                }


            }
            ChapterContext.EDIT_TAKES -> {
                DefaultPluginPreference.defaultPlugin?.let { plugin ->
                    chunk.selectedTake?.let { take ->
                        projectPageActions
                                .launchPluginForTake(take, plugin)
                                .subscribe()
                    }
                }
            }
            else -> {}
        }
    }
}
