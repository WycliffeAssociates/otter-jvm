package org.wycliffeassociates.otter.jvm.app.ui.viewtakes.View

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.VBox
import javafx.scene.shape.Rectangle
import org.wycliffeassociates.otter.jvm.app.UIColorsObject.Colors
import org.wycliffeassociates.otter.jvm.app.ui.viewtakes.Model.Take
import org.wycliffeassociates.otter.jvm.app.ui.viewtakes.ViewModel.ViewTakesViewModel
import tornadofx.*

class ViewTakeView : View() {
    val viewModel: ViewTakesViewModel by inject()
    var dragTarget: VBox by singleAssign()
    var takeToCompare: VBox by singleAssign()
    var selectedTake: VBox by singleAssign()
    var availableTakes: Parent by singleAssign()
    var draggingShadow: Rectangle by singleAssign()
    override val root = anchorpane {
        style {
            backgroundColor += c(Colors["base"])
        }

        setPrefSize(1280.0, 850.0)
        label("Verse 1") {
            anchorpaneConstraints {
                topAnchor = 15.0
                leftAnchor = 10.0
            }
        }
        button(messages["back"], MaterialIconView(MaterialIcon.ARROW_BACK)) {
            anchorpaneConstraints {
                topAnchor = 15.0
                rightAnchor = 10.0
            }
            style {
                backgroundColor+= c(Colors["primary"])
            }
            action {
                workspace.navigateBack()
            }
        }

        hbox(150.0) {
            anchorpaneConstraints {
                leftAnchor = 20.0
                topAnchor = 150.0
            }
            dragTarget = vbox {
                setPrefSize(232.0, 120.0)
                label("Drag Here")
                style {
                    backgroundColor += c(Colors["baseBackground"])
                }
            }
            takeToCompare = vbox {
                setPrefSize(232.0, 120.0)
                label("This is the take to compare")
                style {
                    backgroundColor += c(Colors["tertiary"])
                }
                hbox {
                    button("", MaterialIconView(MaterialIcon.CANCEL)) {
                        action {
                            viewModel.setTake()
                        }
                    }
                    button("", MaterialIconView(MaterialIcon.CHECK))
                }
            }
            dragTarget.hide()
            takeToCompare.hide()

            selectedTake = vbox {
                setPrefSize(232.0, 120.0)
                anchorpaneConstraints {
                    leftAnchor = 20.0
                    topAnchor = 150.0
                }
                style {
                    backgroundColor += c(Colors["primary"])
                    borderRadius += box(10.0.px)
                    backgroundRadius += box(10.0.px)
                }
            }
        }

        availableTakes = createDataGrid()
        availableTakes.apply {
            anchorpaneConstraints {
                leftAnchor = 0.0
                rightAnchor = 0.0
                bottomAnchor = 0.0
            }
        }
        add(availableTakes)

        draggingShadow = rectangle {
            height = 120.0
            width = 232.0
            fill = c(Colors["baseBackground"])
        }
        draggingShadow.hide()

        viewModel.draggingTakeProperty.onChange {
            if (it == true) {
                dragTarget.show()
            } else {
                dragTarget.hide()
                draggingShadow.hide()
            }
        }
        viewModel.dragEvtProperty.onChange {
            if (it != null) {
                val widthOffset = draggingShadow.widthProperty().value / 2
                val heightOffset = draggingShadow.heightProperty().value / 2
                draggingShadow.show()
                draggingShadow.relocate(it.sceneX - widthOffset, it.sceneY - heightOffset)
            }
        }

        viewModel.takeToCompareProperty.onChange {
            if (it == true) {
                takeToCompare.show()

            } else {
                takeToCompare.hide()
            }
        }
    }

    private fun startDrag(evt: MouseEvent) {
        viewModel.startDrag(evt)
    }

    private fun animateDrag(evt: MouseEvent) {
        viewModel.animateDrag(evt)
    }

    private fun cancelDrag(evt: MouseEvent) {
        viewModel.cancelDrag(evt)
    }

    private fun completeDrag(evt: MouseEvent) {
        if (dragTarget.contains(dragTarget.sceneToLocal(evt.sceneX, evt.sceneY))) {
            viewModel.completeDrag(evt)
        } else cancelDrag(evt)
    }

    private fun createDataGrid(): DataGrid<Take> {
        val dataGrid = DataGrid<Take>()
        with(dataGrid) {
            setPrefSize(1200.0,400.0)
            style {
                backgroundColor += c(Colors["base"])
            }
            itemsProperty.bind(viewModel.alternateTakesProperty)
            cellCache {
                cellHeight = 120.0
                cellWidth = 232.0
                vbox {
                    add(label(it.take_num.toString()))
                    setPrefSize(232.0, 120.0)
                    style {
                        alignment = Pos.CENTER
                        backgroundColor += c(Colors["base"])
                        borderRadius += box(10.0.px)
                        backgroundRadius += box(10.0.px)
                        borderWidth += box(1.0.px)
                        borderColor += box(c(Colors["neutral"]))
                        baseColor = c(Colors["base"])
                    }
                    addEventFilter(MouseEvent.MOUSE_DRAGGED, ::startDrag)
                    addEventFilter(MouseEvent.MOUSE_DRAGGED, ::animateDrag)
                    addEventFilter(MouseEvent.MOUSE_RELEASED, ::completeDrag)
                }
            }

        }
        return dataGrid
    }

    init {
        viewModel.takeItems.addAll(availableTakes.childrenUnmodifiable)
    }
}