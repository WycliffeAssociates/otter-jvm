package org.wycliffeassociates.otter.jvm.app.ui.workbook.viewmodel

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import org.wycliffeassociates.otter.common.data.workbook.Chapter
import org.wycliffeassociates.otter.common.data.workbook.Chunk
import org.wycliffeassociates.otter.common.data.workbook.Workbook
import org.wycliffeassociates.otter.common.domain.content.WorkbookFileNamerBuilder
import tornadofx.*
import java.io.File
import java.lang.IllegalStateException

class WorkbookViewModel: ViewModel() {
    val fileNamerBuilder = WorkbookFileNamerBuilder()

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
            fileNamerBuilder.setWorkbook(workbook)
        }
        activeChapterProperty.onChange {
            fileNamerBuilder.setChapter(chapter)
        }
        activeResourceSlugProperty.onChange {
            fileNamerBuilder.setResourceContainerSlug(it)
        }
    }
}