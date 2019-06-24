package org.wycliffeassociates.otter.jvm.app.ui.workbook.viewmodel

import io.reactivex.schedulers.Schedulers
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import org.wycliffeassociates.otter.common.data.workbook.Chapter
import org.wycliffeassociates.otter.common.data.workbook.Chunk
import org.wycliffeassociates.otter.common.data.workbook.Workbook
import org.wycliffeassociates.otter.common.domain.content.FileNamer
import tornadofx.*
import java.io.File
import java.lang.IllegalStateException

class WorkbookViewModel: ViewModel() {
    val activeWorkbookProperty = SimpleObjectProperty<Workbook?>()
    val workbook: Workbook
        get() = activeWorkbookProperty.value ?: throw IllegalStateException("Workbook is null")

    val activeChapterProperty = SimpleObjectProperty<Chapter?>()
    val chapter: Chapter
        get() = activeChapterProperty.value ?: throw IllegalStateException("Chapter is null")

    val activeChunkProperty = SimpleObjectProperty<Chunk?>()
    val chunk: Chunk
        get() = activeChunkProperty.value ?: throw IllegalStateException("Chunk is null")

    val activeResourceSlugProperty = SimpleStringProperty()
    val resourceSlug: String
        get() = activeResourceSlugProperty.value.let {
            return if (it.isNullOrBlank()) throw IllegalStateException("Resource slug is blank or null")
            else it
        }

    val activeProjectAudioDirectoryProperty = SimpleObjectProperty<File?>()
    val projectAudioDirectory: File
        get() = activeProjectAudioDirectoryProperty.value
            ?: throw IllegalStateException("Project audio directory is null")

    init {
        activeWorkbookProperty.onChange {
            FileNamer.Builder.apply {
                bookSlug = it?.target?.slug
                languageSlug = it?.targetLanguageSlug
                it?.target?.chapters?.count()?.subscribe { count ->
                    chapterCount = count
                } // TODO: Is this ok?
            }
        }
        activeChapterProperty.onChange {
            val numChunks = it?.chunks?.count()?.subscribeOn(Schedulers.io())?.blockingGet()
            FileNamer.Builder.apply {
                chapterTitle = it?.title
                chapterSort = it?.sort
                chunkCount = numChunks
                // TODO: Putting it on io and doing blocking get has no effect. should we just keep on ui thread?
//                it?.chunks?.count()?.subscribe { count ->
//                    chunkCount = count
//                } // TODO: Is this ok?
            }
        }
        activeResourceSlugProperty.onChange {
            FileNamer.Builder.apply {
                rcSlug = it
            }
        }
        // The active chunk property does not get set unless the user is working with scripture (not resources)
        activeChunkProperty.onChange {
            FileNamer.Builder.apply {
                start = it?.start
                end = it?.end
            }
        }
    }
}