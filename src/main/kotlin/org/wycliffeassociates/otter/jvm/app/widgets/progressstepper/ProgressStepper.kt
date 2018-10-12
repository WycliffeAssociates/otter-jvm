package org.wycliffeassociates.otter.jvm.app.widgets.progressstepper

import javafx.beans.property.SimpleIntegerProperty
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.layout.*
import javafx.scene.paint.Color
import org.wycliffeassociates.otter.jvm.app.widgets.WidgetsStyles
import tornadofx.*
import tornadofx.Stylesheet.Companion.line
import tornadofx.Stylesheet.Companion.root


class ProgressStepper(private val steps: List<Node>) : HBox() {

    var activeIndex: Int by property(0)
    val activeIndexProperty = getProperty(ProgressStepper::activeIndex)

    init {
        importStylesheet<WidgetsStyles>()
        spaceNodes()
        with(root) {
            vgrow = Priority.ALWAYS
            hgrow = Priority.ALWAYS
            alignment = Pos.CENTER
            anchorpane {


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
                }
            }
            if (step != steps.last()) {
                // Add a completion bar
                line {
                    startX = 0.0
                    startY = 0.0
                    endX = 100.0
                    endY = 0.0
                    addClass(line)
                    activeIndexProperty.onChange {
                        if (it ?: 0 > steps.indexOf(step)) {
                            addPseudoClass("completed")
                        } else {
                            removePseudoClass("completed")
                        }
                    }
                }
            }
        }
    }

    fun setProgress(value: Double) {
        progressValue = value
    }

    fun nextView(index: Int) {
        activeIndex.set(index)
        setProgress((index.toDouble() / (steps.size - 1)))
    }


    private fun spaceNodes() {
        val width = 500.0
        val numNodes = steps.size
        space = width / (numNodes - 1)
    }
}

fun Pane.progressstepper(steps: List<Node>, init: ProgressStepper.() -> Unit): ProgressStepper {
    val ps = ProgressStepper(steps)
    ps.init()
    add(ps)
    return ps
}