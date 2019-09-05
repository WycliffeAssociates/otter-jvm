package org.wycliffeassociates.otter.jvm.app.widgets.workpagelist

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.Control
import org.wycliffeassociates.otter.common.data.workbook.BookElement
import tornadofx.*
abstract class WorkbookList<T> : Fragment() {
    abstract val elementsList: ObservableList<T>
    abstract val workbookPage: Control
}

class BookPage : WorkbookList<BookElement>() {
    override val elementsList: ObservableList<BookElement> = FXCollections.observableArrayList()
    override val workbookPage: Control = DataGrid(elementsList)
    override val root = vbox {
        add(workbookPage)
    }
}

class BookCardMapper {
    companion object {
        fun mapTopChapters(bookElement: BookElement) {
        }

        fun mapToChunks(bookElement: BookElement) {
        }
    }
}