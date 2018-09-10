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
    val viewModel = ViewTakesViewModel(ViewTakesModel())
    var takeToCompare: VBox? = null
    var selectedTake: VBox by singleAssign()
    var availableTakes: Parent by singleAssign()
    override val root = anchorpane {
            // viewModel.rebindOnChange()
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
                    viewModel.selectedTakeProperty.setValue(Take(1, "02/22/13", File("Desktop/3afcc3a2.mp3")))
                    println("yo yo")
                }
            }


            hbox(150.0) {
                anchorpaneConstraints {
                    leftAnchor = 20.0
                    topAnchor = 150.0
                }
                if (viewModel.draggingTake.value == true) {
                    takeToCompare = vbox {
                        setPrefSize(232.0, 120.0)
                        label("Drag Here")
                        style {
                            backgroundColor += c(Colors["baseBackground"])
                        }
                    }
                } else {
                    takeToCompare?.hide()
                }
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
            }

            availableTakes = datagrid(listOf("hhh", "jjj")) {

                cellCache {
                    label(it)

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