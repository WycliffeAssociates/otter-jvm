package org.wycliffeassociates.otter.jvm.app.widgets.progressstepper

import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import org.wycliffeassociates.otter.jvm.app.UIColorsObject.Colors
import tornadofx.*
import tornadofx.Stylesheet.Companion.root


class ProgressStepper(steps: List<Node>) : HBox() {

    var steps: List<Node> = steps
    var views: List<Node> = listOf()
    var space: Double = 0.0

    init {
        spaceNodes()
        with(root) {
            anchorpane{

                stackpane {
                setPrefSize(500.0, 80.0)

                progressbar(0.5) {
                    setPrefSize(500.0, 20.0)
                    setWidth(500.0)
                    hgrow = Priority.ALWAYS
                }
                //hbox {
//                    anchorpaneConstraints {
//                        topAnchor = 40.0
//                        leftAnchor = 0.0
//                    }
//                    style {
//                        borderColor += box(c(Colors["primary"]))
//                        borderWidth += box(1.0.px)
//                    }
//                    alignment = Pos.CENTER
                    steps.forEach {
                        button("", it) {
                            anchorpaneConstraints {
                                leftAnchor = indexInParent*space
                                println(leftAnchor!!)
                                topAnchor = 0.0
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