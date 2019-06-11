package org.wycliffeassociates.otter.jvm.app.ui.workbook.viewmodel

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import org.wycliffeassociates.otter.common.data.workbook.Chapter
import org.wycliffeassociates.otter.common.data.workbook.Chunk
import org.wycliffeassociates.otter.common.data.workbook.Workbook
import tornadofx.*
import java.io.File

class WorkbookViewModel: ViewModel() {
    val activeWorkbookProperty = SimpleObjectProperty<Workbook>()
    val workbook: Workbook
        get() = activeWorkbookProperty.value

    val activeChapterProperty = SimpleObjectProperty<Chapter?>()
    val chapter: Chapter?
        get() = activeChapterProperty.value

    val activeChunkProperty = SimpleObjectProperty<Chunk?>()
    val chunk: Chunk?
        get() = activeChunkProperty.value

    val activeResourceSlugProperty = SimpleStringProperty()
    val resourceSlug: String
        get() = activeResourceSlugProperty.value

    val activeProjectAudioDirectoryProperty = SimpleObjectProperty<File>()
    val projectAudioDirectory: File
        get() = activeProjectAudioDirectoryProperty.value
}