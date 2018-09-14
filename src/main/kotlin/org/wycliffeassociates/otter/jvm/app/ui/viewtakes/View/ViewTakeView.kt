package org.wycliffeassociates.otter.jvm.app.ui.viewtakes.View

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Button
import javafx.scene.effect.DropShadow
import javafx.scene.input.MouseEvent
import javafx.scene.layout.FlowPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
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
    var selectedTake: Node = VBox()
    var placeHolder : VBox by singleAssign()
    var draggingShadow: Node = VBox()
    var recordButton: Button by singleAssign()
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
                if (viewModel.takeToCompareProperty.value != null) {
                    add(viewModel.takeToCompareProperty.value)
                }
                viewModel.takeToCompareProperty.onChange {
                    if (it != null) {
                        add(it)
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

            if (viewModel.selectedTakeProperty.value != null) { //if verse/chunk already has a selectedTake
                selectedTake = viewModel.selectedTakeProperty.value
                add(selectedTake)
                viewModel.selectedTakeProperty.onChange {
                    selectedTake.removeFromParent()
                    selectedTake = it!!
                    add(selectedTake)
                    style {
                        backgroundColor += c(Colors["base"])
                        maxHeight = 120.0.px
                        vgrow = Priority.NEVER
                    }
                }
            } else {
                placeHolder = vbox { //used if there is no current selectedTake
                    style {
                        backgroundColor += c(Colors["neutralTone"])
                        borderRadius += box(10.0.px)
                        backgroundRadius += box(10.0.px)
                    }
                    setPrefSize(232.0, 120.0)
                    anchorpaneConstraints {
                        leftAnchor = 20.0
                        topAnchor = 150.0
                    }
                    vgrow = Priority.NEVER
                }
                selectedTake = vbox {
                    viewModel.selectedTakeProperty.onChange {
                        clear()
                        placeHolder.removeFromParent()
                        add(it!!)
                        style {
                            backgroundColor += c(Colors["base"])
                            maxHeight = 120.0.px
                            vgrow = Priority.NEVER
                        }
                    }
                }
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

        var recordIcon = MaterialIconView(MaterialIcon.MIC_NONE, "25px")
        recordButton = button("",recordIcon) {
            anchorpaneConstraints {
                bottomAnchor = 25.0
                rightAnchor = 25.0
            }
            style {
                backgroundColor += c(Colors["base"])
                recordIcon.fill = c(Colors["primary"])
                backgroundRadius += box(100.0.px)
                borderRadius += box(100.0.px)
                prefHeight = 50.px
                prefWidth = 50.px
                effect = DropShadow(10.0, Color.GRAY)

            }
        }

        draggingShadow = vbox() {
            viewModel.draggingShadowProperty.onChange {
                if (it != null) {
                    clear()
                    add(it)
                }
            }
            add(viewModel.draggingShadow)
        }
       draggingShadow.hide()


        viewModel.draggingTakeProperty.onChange {//is a take being dragged right now
            if (it == true) {
                dragTarget.show()
            } else {
                dragTarget.hide()
                draggingShadow.hide()
                if(viewModel.comparingTakeProperty.value == false) { // false means this drag event ended with a drag cancel
                    resetGrid()
                }
                draggingShadow.toFront()
            }
        }
        viewModel.mousePositionProperty.onChange {//update position of drag shadow when mouse position changes during dragging
            if (it != null) {
                val widthOffset = 116
                val heightOffset = 120/ 2
                draggingShadow.toFront()
                draggingShadow.show()
                draggingShadow.relocate(it[0] - widthOffset , it[1] - heightOffset)
            }
        }

        viewModel.comparingTakeProperty.onChange { //is a take being compared right now
            if (it == true) {
                takeToCompare.show()

            } else {
                takeToCompare.hide()
                resetGrid()

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
        val flowpane = FlowPane()
        with(flowpane) {
            setPrefSize(1200.0, 400.0)
            vgap = 16.0
            hgap = 16.0
            style {
                backgroundColor += c(Colors["base"])
                spacing = 10.0.px
            }
            viewModel.alternateTakesProperty.value.forEach {
                vbox {
                        add(TakeCard(232.0, 120.0, TakeCardViewModel(TakeCardModel(it))))
                        addEventHandler(MouseEvent.MOUSE_DRAGGED, ::startDrag)
                        addEventHandler(MouseEvent.MOUSE_DRAGGED, ::animateDrag)
                        addEventHandler(MouseEvent.MOUSE_RELEASED, ::completeDrag)
                }
             }
        }
        return flowpane
    }

    private fun resetGrid() {
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