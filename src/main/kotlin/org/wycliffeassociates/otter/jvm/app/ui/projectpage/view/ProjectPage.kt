package org.wycliffeassociates.otter.jvm.app.ui.projectpage.view

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.control.ListView
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
    private var childrenList = ListView<Collection>()

    init {
        viewModel.setWorkspace(workspace)
    }

    override fun onDock() {
        super.onDock()
        // Make sure we refresh the chunks if need be
        if (viewModel.chunks.isNotEmpty()) {
            childrenList.selectedItem?.let { viewModel.selectChildCollection(it) }
        }
    }

    override val root = stackpane {
        hbox {
            vbox {
                label {
                    prefHeight = 100.0
                    textProperty().bind(viewModel.projectTitleProperty)
                }
                childrenList = listview<Collection> {
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

        // Plugin active cover
        stackpane {
            style {
                alignment = Pos.CENTER
                backgroundColor += Color.BLACK
                        .deriveColor(0.0, 0.0, 0.0, 0.5)
            }
            val icon = MaterialIconView(MaterialIcon.MIC_NONE, "60px")
                    .apply {
                        style(true) {
                            fill = Color.WHITE
                        }
                        // Update the icon when the context changes
                        viewModel.contextProperty.onChange { newContext ->
                            when(newContext) {
                                ChapterContext.RECORD -> setIcon(MaterialIcon.MIC_NONE)
                                ChapterContext.EDIT_TAKES -> setIcon(MaterialIcon.EDIT)
                                else -> {}
                            }
                        }
                    }
            add(icon)
            progressindicator {
                style {
                    maxWidth = 125.px
                    maxHeight = 125.px
                    progressColor = Color.WHITE
                }
            }
            visibleProperty().bind(viewModel.showPluginActiveProperty)
        }
    }

    private fun createDataGrid(): DataGrid<Chunk> {
        val dataGrid = DataGrid<Chunk>()
        with(dataGrid) {
            items = viewModel.chunks
            vgrow = Priority.ALWAYS
            cellCache {
                val chunkCard = ChunkCard(it)
                with(chunkCard) {
                    actionButton.action {
                        viewModel.doChunkContextualAction(chunk)
                    }
                }
                when (viewModel.contextProperty.value ?: ChapterContext.RECORD) {
                    ChapterContext.RECORD -> {
                        with(chunkCard) {
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
                        with(chunkCard) {
                            actionButton.apply {
                                viewModel
                                        .checkIfChunkHasTakes(chunk)
                                        .observeOn(JavaFxScheduler.platform())
                                        .subscribe { hasTakes ->
                                            if (hasTakes) {
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
                    }
                    ChapterContext.EDIT_TAKES -> {
                        with(chunkCard) {
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
                chunkCard
            }
        }
        return dataGrid
    }
}


