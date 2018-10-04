package org.wycliffeassociates.otter.jvm.app.ui.projectpage.model

import io.reactivex.Single
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler
import io.reactivex.schedulers.Schedulers
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.common.data.audioplugin.AudioPluginData
import org.wycliffeassociates.otter.common.data.model.Chunk
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.common.data.model.Take
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import org.wycliffeassociates.otter.jvm.app.ui.projectpage.view.ChapterContext
import org.wycliffeassociates.otter.jvm.device.audioplugin.AudioPlugin
import org.wycliffeassociates.otter.jvm.persistence.repositories.ProjectRepository
import tornadofx.getProperty
import tornadofx.property
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.time.LocalDate
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem

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

    var pluginOptions: ObservableList<AudioPluginData> = FXCollections.observableList(mutableListOf())

    init {
        // TODO: Get from use case
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
                    Injector.projectRepo.getChildren(it)
                }
                .observeOn(JavaFxScheduler.platform())
                .doOnSuccess {
                    // Now we have the children of the project collection
                    children.clear()
                    children.addAll(it)
                }
                .subscribe()

        // TODO: Get from use case
        Injector
                .pluginRepository
                .getAll()
                .observeOn(JavaFxScheduler.platform())
                .subscribe { pluginDataList ->
                    pluginOptions.clear()
                    pluginOptions.addAll(pluginDataList)
                }
    }

    fun selectChildCollection(child: Collection) {
        // Remove existing chunks so the user knows they are outdated
        chunks.clear()
        Injector
                .chunkRepository
                .getByCollection(child)
                .observeOn(JavaFxScheduler.platform())
                .subscribe { retrieved ->
                    chunks.clear() // Make sure any chunks that might have been added are removed
                    chunks.addAll(retrieved)
                }
    }

    fun checkIfChunkHasTakes(chunk: Chunk): Single<Boolean> {
        return Injector
                .takeRepository
                .getByChunk(chunk)
                .map { it.isNotEmpty() }
    }

    fun doChunkContextualAction(chunk: Chunk) {
        when (context) {
            ChapterContext.RECORD -> {
                // Get existing takes
                Injector
                        .takeRepository
                        .getByChunk(chunk)
                        .map {
                            // Create a file for this take
                            val takeFile = Injector
                                    .directoryProvider
                                    .getUserDataDirectory(
                                            listOf("projects", "project${project?.id ?: 0}")
                                                    .joinToString(File.separator)
                                    ).resolve(File("chunk${chunk.id}_take${it.size + 1}.wav"))

                            val newTake = Take(
                                    takeFile.name,
                                    takeFile,
                                    it.size+1,
                                    LocalDate.now(),
                                    false,
                                    listOf()
                            )

                            // Create an empty WAV file
                            AudioSystem.write(
                                    AudioInputStream(
                                            ByteArrayInputStream(ByteArray(0)),
                                            AudioFormat(
                                                    44100.0f,
                                                    16,
                                                    1,
                                                    true,
                                                    false
                                            ),
                                            0
                                    ),
                                    AudioFileFormat.Type.WAVE,
                                    newTake.path
                            )
                            val plugin = AudioPlugin(pluginOptions.first())
                            plugin
                                    .launch(newTake.path)
                                    .blockingAwait()
                            newTake
                        }
                        .flatMap { newTake ->
                            // They finished recording
                            // Put the take in the database
                            Injector
                                    .takeRepository
                                    .insertForChunk(newTake, chunk)
                        }
                        .subscribe()
            }
            ChapterContext.EDIT_TAKES -> {
                chunk.selectedTake?.let { take ->
                    val plugin = AudioPlugin(pluginOptions.first())
                    plugin
                            .launch(take.path)
                            .subscribe()
                }
            }
            else -> {}
        }
        // Update the chunks with any new information

    }
}
