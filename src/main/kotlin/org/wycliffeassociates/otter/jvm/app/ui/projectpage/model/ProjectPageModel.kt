package org.wycliffeassociates.otter.jvm.app.ui.projectpage.model

import com.github.thomasnield.rxkotlinfx.observeOnFx
import io.reactivex.Single
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

    // Selected child
    var activeChild: Collection by property()
    val activeChildProperty = getProperty(ProjectPageModel::activeChild)

    // List of chunks to display on the screen
    var chunks: ObservableList<Chunk> = FXCollections.observableList(mutableListOf())

    // What record/review/edit context are we in?
    var context: ChapterContext by property(ChapterContext.RECORD)
    var contextProperty = getProperty(ProjectPageModel::context)

    // Whether the UI should show the plugin as active
    var showPluginActive: Boolean by property(false)
    var showPluginActiveProperty = getProperty(ProjectPageModel::showPluginActive)

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
                .observeOnFx()
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
                .observeOnFx()
                .doOnSuccess {
                    // Now we have the children of the project collection
                    children.clear()
                    children.addAll(it)
                }
                .subscribe()
    }

    fun selectChildCollection(child: Collection) {
        activeChild = child
        // Remove existing chunks so the user knows they are outdated
        chunks.clear()
        projectPageActions
                .getChunks(child)
                .observeOnFx()
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
                        showPluginActive = true
                        projectPageActions
                                .createNewTake(chunk, project, activeChild)
                                .flatMap { take ->
                                    projectPageActions
                                            .launchPluginForTake(take, plugin)
                                            .toSingle { take }
                                }
                                .flatMap {take ->
                                    projectPageActions.insertTake(take, chunk)
                                }
                                .observeOnFx()
                                .subscribe { _ ->
                                    showPluginActive = false
                                }
                    }
                }


            }
            ChapterContext.VIEW_TAKES -> {
                // Launch the select takes page
            }
            ChapterContext.EDIT_TAKES -> {
                DefaultPluginPreference.defaultPlugin?.let { plugin ->
                    chunk.selectedTake?.let { take ->
                        showPluginActive = true
                        projectPageActions
                                .launchPluginForTake(take, plugin)
                                .observeOnFx()
                                .subscribe {
                                    showPluginActive = false
                                }
                    }
                }
            }
        }
    }
}
