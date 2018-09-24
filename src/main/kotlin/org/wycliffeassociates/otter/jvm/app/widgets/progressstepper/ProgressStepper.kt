package org.wycliffeassociates.otter.jvm.app.widgets.progressstepper

import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Pos
import javafx.scene.Node
<<<<<<< HEAD
import javafx.scene.control.Button
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import org.wycliffeassociates.otter.jvm.app.UIColorsObject.Colors
=======
import javafx.scene.layout.*
import org.wycliffeassociates.otter.jvm.app.widgets.WidgetsStyles
>>>>>>> start adding validation
import tornadofx.*
import tornadofx.Stylesheet.Companion.root


class ProgressStepper(steps: List<Node>) : HBox() {

    var steps: List<Node> = steps
    var views: List<Node> = listOf()
    var space: Double = 0.0

    init {
        importStylesheet<WidgetsStyles>()
        spaceNodes()
        with(root) {
            anchorpane{

                stackpane {

                    setPrefSize(500.0, 80.0)
                    progressbar(0.0) {
                        addClass(WidgetsStyles.progressStepperBar)
                         progressValueProperty.onChange {
                            if(it != null) {
                                progress =it
                            }
                        }
                        setPrefSize(steps.size * space, 20.0)
                        setWidth(500.0)
                        hgrow = Priority.ALWAYS
                    }
                    hbox(space) {
                        anchorpaneConstraints {
                            topAnchor = 40.0
                            leftAnchor = 0.0
                        }
                        alignment = Pos.CENTER
                        steps.forEach {
                            var icon = it
                            button("", icon) {
                                style {
                                    backgroundRadius += box(20.0.px)
                                    prefHeight = 32.0.px
                                    prefWidth = 32.0.px
                                }
                                action {
                                    if (indexInParent < activeIndexProperty) {
                                        nextView(indexInParent)
                                    }
                                }
                            }
                        }
                    }
//                }
            }

            }
        }
    }

    fun spaceNodes() {
        val width = 500.0
        val numNodes = steps.size
        space = width/(numNodes -1)

    }

    fun addStep(text: String, icon: MaterialIconView, view: Node) {
        val bttn = Button("", icon)
        val tempVBox = vbox {
            add(bttn)
            add(label(text))
        }
//        steps.add(tempVBox)
//        views.add(view)
    }
}

fun Pane.progressstepper(steps: List<Node>, init: ProgressStepper.() -> Unit): ProgressStepper {
    val ps = ProgressStepper(steps)
//    ps.steps = steps
    ps.init()
    add(ps)
    return ps
}