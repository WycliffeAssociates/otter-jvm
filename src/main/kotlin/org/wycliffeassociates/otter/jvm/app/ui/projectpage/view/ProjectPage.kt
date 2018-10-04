package org.wycliffeassociates.otter.jvm.app.ui.projectpage.view

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Orientation
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import org.wycliffeassociates.otter.common.data.model.Chunk
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.jvm.app.UIColorsObject.Colors
import org.wycliffeassociates.otter.jvm.app.ui.projectpage.viewmodel.ProjectPageViewModel
import org.wycliffeassociates.otter.jvm.app.widgets.*
import tornadofx.*

class ProjectPage : View() {
    private val viewModel: ProjectPageViewModel by inject()
    private var chunkGrid = createDataGrid()

    override val root = hbox {
        vbox {
            label {
                prefHeight = 100.0
                textProperty().bind(viewModel.projectTitleProperty)
            }
            listview<Collection> {
                items = viewModel.children
                cellCache {
                    // TODO: Localize string
                    label(it.titleKey)
                }
                vgrow = Priority.ALWAYS
                selectionModel.selectedItemProperty().onChange {
                    // Tell the view model which child was selected
                    if (it != null) viewModel.selectChildCollection(it)
                }
            }
        }

        vbox {
            hgrow = Priority.ALWAYS
            vbox {
                viewModel.contextProperty.onChange {
                    chunkGrid.removeFromParent()
                    chunkGrid = createDataGrid()
                    add(chunkGrid)
                }
                add(chunkGrid)
            }
            listmenu {
                orientation = Orientation.HORIZONTAL
                useMaxWidth = true
                style {
                    backgroundColor += Color.WHITE
                }
                item(graphic = MaterialIconView(MaterialIcon.MIC_NONE, "25px")) {
                    activeItem = this
                    whenSelected { viewModel.changeContext(ChapterContext.RECORD) }
                    style {
                        backgroundColor += c(Colors["primary"])
                        padding = box(20.px)
                    }
                    parent.layoutBoundsProperty().onChange { newBounds ->
                        newBounds?.let { prefWidth = it.width / items.size }
                    }
                }
                item(graphic = MaterialIconView(MaterialIcon.APPS, "25px")) {
                    whenSelected { viewModel.changeContext(ChapterContext.VIEW_TAKES) }
                    style {
                        backgroundColor += c(Colors["secondary"])
                        padding = box(20.px)
                    }
                    parent.layoutBoundsProperty().onChange { newBounds ->
                        newBounds?.let { prefWidth = it.width / items.size }
                    }
                }
                item(graphic = MaterialIconView(MaterialIcon.EDIT, "25px")) {
                    whenSelected { viewModel.changeContext(ChapterContext.EDIT_TAKES) }
                    style {
                        backgroundColor += c(Colors["tertiary"])
                        padding = box(20.px)
                    }
                    parent.layoutBoundsProperty().onChange { newBounds ->
                        newBounds?.let { prefWidth = it.width / items.size }
                    }
                }
            }
        }
    }

    private fun createDataGrid(): DataGrid<Chunk> {
        val dataGrid = DataGrid<Chunk>()
        with(dataGrid) {
            items = viewModel.chunks
            vgrow = Priority.ALWAYS
            cellCache {
                val verseCard = ChunkCard(it)
                when (viewModel.contextProperty.value ?: ChapterContext.RECORD) {
                    ChapterContext.RECORD -> {
                        with(verseCard) {
                            actionButton.apply {
                                graphic = MaterialIconView(MaterialIcon.MIC_NONE)
                                text = messages["record"]
                                style {
                                    if (chunk.selectedTake != null) {
                                        backgroundColor += c(Colors["base"])
                                        textFill = c(Colors["primary"])
                                        borderColor += box(c(Colors["primary"]))
                                    } else {
                                        backgroundColor += c(Colors["primary"])
                                    }
                                }
                            }
                        }
                    }
                    ChapterContext.VIEW_TAKES -> {
                        with(verseCard) {
                            actionButton.apply {
                                if (chunk.selectedTake != null) {
                                    graphic = MaterialIconView(MaterialIcon.APPS)
                                    text = messages["viewTakes"]
                                    style {
                                        backgroundColor += c(Colors["secondary"])
                                    }
                                } else {
                                    actionButton.hide()
                                    style {
                                        backgroundColor += c(Colors["baseBackground"])
                                    }
                                }
                            }
                        }
                    }
                    ChapterContext.EDIT_TAKES -> {
                        with(verseCard) {
                            if (chunk.selectedTake != null) {
                                actionButton.apply {
                                    graphic = MaterialIconView(MaterialIcon.EDIT)
                                    text = messages["edit"]
                                    style {
                                        backgroundColor += c(Colors["tertiary"])
                                    }
                                }
                            } else {
                                actionButton.hide()
                                style {
                                    backgroundColor += c(Colors["baseBackground"])
                                }
                            }
                        }
                    }
                }
                verseCard
            }
        }
        return dataGrid
    }
}


