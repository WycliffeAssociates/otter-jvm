package org.wycliffeassociates.otter.jvm.app.ui.workbook.viewmodel

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import org.wycliffeassociates.otter.common.data.workbook.Chapter
import org.wycliffeassociates.otter.common.data.workbook.Chunk
import org.wycliffeassociates.otter.common.data.workbook.Workbook
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import tornadofx.*
import java.io.File
import java.lang.IllegalStateException

class WorkbookViewModel: ViewModel() {
    private val injector: Injector by inject()
    private val directoryProvider = injector.directoryProvider

    val activeWorkbookProperty = SimpleObjectProperty<Workbook>()
    val workbook: Workbook
        get() = activeWorkbookProperty.value ?: throw IllegalStateException("Workbook is null")

    val activeChapterProperty = SimpleObjectProperty<Chapter>()
    val chapter: Chapter
        get() = activeChapterProperty.value ?: throw IllegalStateException("Chapter is null")

    val activeChunkProperty = SimpleObjectProperty<Chunk>()
    val chunk: Chunk? by activeChunkProperty

    val activeResourceSlugProperty = SimpleStringProperty()
    val resourceSlug: String
        get() = activeResourceSlugProperty.value.let {
            return if (it.isNullOrBlank()) throw IllegalStateException("Resource slug is blank or null")
            else it
        }

    val activeProjectAudioDirectoryProperty = SimpleObjectProperty<File>()
    val projectAudioDirectory: File
        get() = activeProjectAudioDirectoryProperty.value
            ?: throw IllegalStateException("Project audio directory is null")

    init {
        activeWorkbookProperty.onChange { workbook ->
            workbook?.let {
                activeProjectAudioDirectoryProperty.set(getProjectAudioDirectory(workbook))
            }
        }
    }

    private fun getProjectAudioDirectory(workbook: Workbook): File {
        val path = directoryProvider.getUserDataDirectory(
            "testAudioPath\\" +
                    "${workbook.target.language.slug}\\" +
                    "${workbook.target.slug}\\"
        )
        path.mkdirs()
        return path
    }
}