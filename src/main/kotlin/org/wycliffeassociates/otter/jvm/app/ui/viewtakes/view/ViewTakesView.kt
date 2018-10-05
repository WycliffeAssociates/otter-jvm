package org.wycliffeassociates.otter.jvm.app.ui.viewtakes.view

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.effect.DropShadow
import javafx.scene.input.MouseEvent
import javafx.scene.layout.FlowPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import org.wycliffeassociates.otter.common.data.model.Take
import org.wycliffeassociates.otter.jvm.app.UIColorsObject.Colors
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import org.wycliffeassociates.otter.jvm.app.ui.viewtakes.viewmodel.ViewTakesViewModel
import org.wycliffeassociates.otter.jvm.app.widgets.TakeCard
import tornadofx.*

class ViewTakesView : View() {
    val viewModel: ViewTakesViewModel by inject()

    // The currently selected take
    var selectedTakeProperty = SimpleObjectProperty<TakeCard>()
    // Take at the top to compare to an existing selected take
    var proposedTakeProperty = SimpleObjectProperty<TakeCard>()
    var draggingTakeProperty = SimpleObjectProperty<TakeCard>()
    var proposedTakeContainer = VBox()

    // Drag target to show when drag action in progress
    var dragTarget: VBox by singleAssign()

    // Drag shadow (node that actually moves with cursor)
    var dragShadow: Node = VBox()

    // Record button?
    var recordButton: Button by singleAssign()

    // Flow pane of available takes
    private var takesFlowPane = createTakesFlowPane()

    override val root = anchorpane {
        style {
            backgroundColor += c(Colors["base"])
        }

        // Title label
        label(viewModel.titleProperty) {
            anchorpaneConstraints {
                topAnchor = 15.0
                leftAnchor = 10.0
            }
            style {
                fontSize = 40.px
            }
        }

        // Back button
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

        // Top items above the alternate takes
        // Drag target and/or selected take
        hbox(150.0) {
            anchorpaneConstraints {
                leftAnchor = 20.0
                topAnchor = 150.0
            }

            // Create the drag target
            dragTarget = vbox {
                setPrefSize(232.0, 120.0)
                label("Drag Here")
                style {
                    backgroundColor += c(Colors["baseBackground"])
                    borderRadius += box(10.0.px)
                    backgroundRadius += box(10.0.px)
                }
                // Initially hide the drag target
                hide()
                draggingTakeProperty.onChange {
                    if (it == null) {
                        // Nothing to drag
                        hide()
                    } else {
                        // Something is being dragged
                        show()
                    }
                }
            }

            // Container for proposed take and buttons
            proposedTakeContainer = vbox {
                proposedTakeProperty.onChange {
                    if (it != null) {
                        add(it)
                        // Create the accept/reject buttons
                        var actionButtons = HBox()
                        actionButtons = hbox(10.0) {
                            button("", MaterialIconView(MaterialIcon.CANCEL)) {
                                action {
                                    viewModel.rejectProposedTake()
                                    actionButtons.removeFromParent()
                                    // Add back to the flow pane
                                    takesFlowPane.add(it)
                                    proposedTakeProperty.value = null
                                }
                            }
                            button("", MaterialIconView(MaterialIcon.CHECK)) {
                                action {
                                    viewModel.acceptProposedTake()
                                    actionButtons.removeFromParent()
                                    // Move the old selected take back to the flow pane
                                    if (selectedTakeProperty.value != null) {
                                        takesFlowPane.add(selectedTakeProperty.value)
                                    }
                                    // Put in selected take
                                    viewModel.selectedTakeProperty.value = it.take
                                    proposedTakeProperty.value = null
                                }
                            }
                        }
                    } else {
                        // No more proposed take
                        clear()
                    }
                }
            }

            // Does a selected take exist?
            vbox {
                // Check if the selected take card has changed
                val placeholder = vbox {
                    style {
                        backgroundColor += c(Colors["neutralTone"])
                        borderRadius += box(10.px)
                        backgroundRadius += box(10.px)
                    }
                    setMinSize(232.0, 120.0)
                    anchorpaneConstraints {
                        leftAnchor = 20.0
                        topAnchor = 150.0
                    }
                    vgrow = Priority.NEVER
                }

                selectedTakeProperty.onChange {
                    clear()
                    if (it == null) {
                        // No currently selected take
                        add(placeholder)
                    } else {
                        // Add the selected take card
                        viewModel.selectedTakeProperty.value = it.take
                        add(it)
                    }
                }

                viewModel.selectedTakeProperty.addListener { _, _, it ->
                    // The view model wants us to use this selected take
                    // This take will not appear in the flow pane items
                    if (it != null && selectedTakeProperty.value == null) {
                        selectedTakeProperty.value = createTakeCard(it)
                    } else selectedTakeProperty.value = null
                }
            }
        }

        // Setup the available takes flow pane constraints
        add(takesFlowPane)

        // Record button?
        val recordIcon = MaterialIconView(MaterialIcon.MIC_NONE, "25px")
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

        // Create drag shadow node and hide it initially
        dragShadow = vbox {
            draggingTakeProperty.onChange {
                clear()
                if (it != null) {
                    add(it)
                    show()
                } else {
                    hide()
                }
            }
            hide()
            addEventHandler(MouseEvent.MOUSE_DRAGGED, ::animateDrag)
            addEventHandler(MouseEvent.MOUSE_RELEASED, ::completeDrag)
        }
    }

    private fun startDrag(evt: MouseEvent) {
        // Get the take being dragged
        val target = evt.target as Node

        // Remove from the flow pane
        val takeCard = target.findParentOfType(TakeCard::class) as TakeCard
        if (takeCard.parent == takesFlowPane) {
            takeCard.removeFromParent()
            draggingTakeProperty.value = takeCard
        }
        animateDrag(evt)
    }

    private fun animateDrag(evt: MouseEvent) {
        if (draggingTakeProperty.value != null) {
            val widthOffset = 116
            val heightOffset = 120 / 2
            dragShadow.toFront()
            dragShadow.relocate(evt.sceneX - widthOffset, evt.sceneY - heightOffset)
        }
    }

    private fun cancelDrag(evt: MouseEvent) {
        takesFlowPane.add(draggingTakeProperty.value)
        draggingTakeProperty.value = null
    }

    private fun completeDrag(evt: MouseEvent) {
        if (dragTarget.contains(dragTarget.sceneToLocal(evt.sceneX, evt.sceneY))) {
            proposedTakeProperty.value = draggingTakeProperty.value
            draggingTakeProperty.value = null
        } else cancelDrag(evt)
    }

    // Create the flow pane of alternate takes
    private fun createTakesFlowPane(): FlowPane {
        val flowpane = FlowPane()
        flowpane.apply {
            setPrefSize(1200.0, 400.0)
            vgap = 16.0
            hgap = 16.0
            style {
                backgroundColor += c(Colors["base"])
                spacing = 10.px
            }
            anchorpaneConstraints {
                leftAnchor = 0.0
                rightAnchor = 0.0
                bottomAnchor = 0.0
            }
            // Update the takes displayed
            viewModel.alternateTakes.onChange {
                clear()
                it.list.forEach {
                    // Add a new take card
                    add(createTakeCard(it))
                }
            }
        }
        return flowpane
    }

    private fun createTakeCard(take: Take): TakeCard {
        return TakeCard(take,Injector.audioPlayer).apply {
            style(true) {
                borderColor += box(Color.BLACK)
                borderWidth += box(1.px)
                borderRadius += box(10.px)
            }
            addEventHandler(MouseEvent.MOUSE_PRESSED, ::startDrag)
        }
    }
}