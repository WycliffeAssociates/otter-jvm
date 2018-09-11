package org.wycliffeassociates.otter.jvm.app.ui.viewtakes.View

import com.sun.javafx.binding.ContentBinding.bind
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.shape.Rectangle
import org.wycliffeassociates.otter.jvm.app.UIColorsObject.Colors
import org.wycliffeassociates.otter.jvm.app.ui.viewtakes.Model.Take
import org.wycliffeassociates.otter.jvm.app.ui.viewtakes.Model.ViewTakesModel
import org.wycliffeassociates.otter.jvm.app.ui.viewtakes.ViewModel.ViewTakesViewModel
import tornadofx.*
import java.io.File

class ViewTakeView : View() {
    val viewModel: ViewTakesViewModel by inject()
    var takeToCompare: VBox? = null
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
        button("Back") {
            anchorpaneConstraints {
                topAnchor = 15.0
                rightAnchor = 10.0
            }
            action {
                //                    viewModel.selectedTakeProperty.setValue(Take(1, "02/22/13", File("Desktop/3afcc3a2.mp3")))

            }
        }

        hbox(150.0) {
            anchorpaneConstraints {
                leftAnchor = 20.0
                topAnchor = 150.0
            }
            takeToCompare = vbox {
                setPrefSize(232.0, 120.0)
                label("Drag Here")
                style {
                    backgroundColor += c(Colors["baseBackground"])
                }
            }
            takeToCompare?.hide()

            selectedTake = vbox {
                setPrefSize(232.0, 120.0)
                anchorpaneConstraints {
                    leftAnchor = 20.0
                    topAnchor = 150.0
                }
                style {
                    backgroundColor += c(Colors["primary"])
                }
            }

            viewModel.draggingTaleProperty.onChange {
                if (it == true) {
                    takeToCompare?.show()
                } else {
                    takeToCompare?.hide()
                    draggingShadow.hide()
                }
            }
        }

        availableTakes = datagrid(listOf("hhh", "jjj")) {

            cellCache {
                vbox {
                    label(it)
                    setPrefSize(232.0, 120.0)
                    style {
                        backgroundColor += c(Colors["base"])
                    }
                }
            }
            anchorpaneConstraints {
                bottomAnchor = 0.0
                rightAnchor = 0.0
                leftAnchor = 0.0
            }
            style {
                backgroundColor += c(Colors["base"])
            }
            prefHeight = 300.0
            vgrow = Priority.ALWAYS
        }
        addEventFilter(MouseEvent.MOUSE_PRESSED, ::startDrag)
        addEventFilter(MouseEvent.MOUSE_DRAGGED, ::animateDrag)
        addEventFilter(MouseEvent.MOUSE_RELEASED, ::cancelDrag)
        addEventFilter(MouseEvent.MOUSE_RELEASED, ::completeDrag)


        draggingShadow = rectangle {
            height = 120.0
            width = 232.0
            style {
                backgroundColor += c(Colors["base"])
            }
        }
        draggingShadow.hide()

        viewModel.dragEvtProperty.onChange {
            if (it != null) {
                val widthOffset = draggingShadow.widthProperty().value / 2
                val heightOffset = draggingShadow.heightProperty().value / 2
                draggingShadow.show()
                draggingShadow.relocate(it.sceneX - widthOffset, it.sceneY - heightOffset)
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
        viewModel.completeDrag(evt)
    }

    init {
        viewModel.takeItems.addAll(availableTakes.childrenUnmodifiable)
    }
}