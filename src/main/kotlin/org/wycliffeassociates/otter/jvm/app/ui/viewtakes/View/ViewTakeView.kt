package org.wycliffeassociates.otter.jvm.app.ui.viewtakes.View

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.scene.Parent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.FlowPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.shape.Rectangle
import org.wycliffeassociates.otter.jvm.app.UIColorsObject.Colors
import org.wycliffeassociates.otter.jvm.app.ui.viewtakes.ViewModel.ViewTakesViewModel
import org.wycliffeassociates.otter.jvm.app.widgets.takecard.TakeCard
import org.wycliffeassociates.otter.jvm.app.widgets.takecard.TakeCardModel
import org.wycliffeassociates.otter.jvm.app.widgets.takecard.TakeCardViewModel
import tornadofx.*

class ViewTakeView : View() {
    val viewModel: ViewTakesViewModel by inject()
    var dragTarget: VBox by singleAssign()
    var takeToCompare: VBox by singleAssign()
    var selectedTake: VBox by singleAssign()
    var draggingShadow: Rectangle by singleAssign()
    private var availableTakes = createFlowPane()

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
            style {
                fontSize = 40.px
            }
        }
        button(messages["back"], MaterialIconView(MaterialIcon.ARROW_BACK)) {
            anchorpaneConstraints {
                topAnchor = 15.0
                rightAnchor = 10.0
            }
            style {
                backgroundColor += c(Colors["primary"])
                minWidth = 232.0.px
                textFill = c(Colors["base"])
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
                    borderRadius += box(10.0.px)
                    backgroundRadius += box(10.0.px)
                }
            }
            takeToCompare = vbox {
                var actionButtons = HBox()
                if (viewModel.takeToCompare != null) {
                    add(viewModel.takeToCompare)
                }
                viewModel.takeToCompareProperty.onChange {
                    if (it != null) {
                        add(viewModel.takeToCompare)
                        actionButtons = hbox(10.0) {
                            button("", MaterialIconView(MaterialIcon.CANCEL)) {
                                action {
                                    viewModel.cancelSetTake()
                                    actionButtons.removeFromParent()
                                }
                            }
                            button("", MaterialIconView(MaterialIcon.CHECK)) {
                                action {
                                    viewModel.setTake()
                                    actionButtons.removeFromParent()
                                }
                            }
                        }
                    }
                }

            }
            dragTarget.hide()
            takeToCompare.hide()

            selectedTake = vbox {
                if (viewModel.selectedTake != null) {
                add(viewModel.selectedTake)
                selectedTake.removeFromParent()
            }
                else {
                    style {
                        backgroundColor += c(Colors["neutralTone"])
                        borderRadius += box(10.0.px)
                        backgroundRadius += box(10.0.px)
                    }
                }
                setPrefSize(232.0, 120.0)
                anchorpaneConstraints {
                    leftAnchor = 20.0
                    topAnchor = 150.0
                }
                vgrow = Priority.NEVER
            }

            viewModel.selectedTakeProperty.onChange {
                selectedTake.removeFromParent()
                add(viewModel.selectedTake)
                style {
                    backgroundColor += c(Colors["base"])
                }
            }
        }

        viewModel.comparingTakeProperty.onChange {
            if (it == false) {
                availableTakes.removeFromParent()
                availableTakes = createFlowPane()
                availableTakes.apply {
                    anchorpaneConstraints {
                        leftAnchor = 0.0
                        rightAnchor = 0.0
                        bottomAnchor = 0.0
                    }
                }
                add(availableTakes)

            }
        }
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
            fill = c(Colors["neutralTone"])
            style {
                borderRadius += box(10.0.px)
                backgroundRadius += box(10.0.px)
            }
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

        viewModel.comparingTakeProperty.onChange {
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

    private fun createFlowPane(): FlowPane {
        val dataGrid = FlowPane()
        with(dataGrid) {
            setPrefSize(1200.0, 400.0)
            vgap = 16.0
            hgap = 16.0
            style {
                backgroundColor += c(Colors["base"])
                spacing = 10.0.px
            }
            viewModel.alternateTakes.forEach {
                vbox {
                    add(TakeCard(232.0, 120.0, TakeCardViewModel(TakeCardModel(it))))
                    addEventHandler(MouseEvent.MOUSE_DRAGGED, ::startDrag)
                    addEventHandler(MouseEvent.MOUSE_DRAGGED, ::animateDrag)
                    addEventHandler(MouseEvent.MOUSE_RELEASED, ::completeDrag)
                }
                viewModel.takeItems.add(TakeCard(232.0, 120.0, TakeCardViewModel(TakeCardModel(it))))
            }


        }
        return dataGrid
    }

    init {
        // viewModel.takeItems.addAll()
    }
}