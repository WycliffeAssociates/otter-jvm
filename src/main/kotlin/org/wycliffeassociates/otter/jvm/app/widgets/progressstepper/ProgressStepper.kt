package org.wycliffeassociates.otter.jvm.app.widgets.progressstepper

import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import tornadofx.*


class ProgressStepper: HBox() {

    var steps: MutableList<VBox> = mutableListOf()
    var views: MutableList<Node> = mutableListOf()

    init {

    }

    fun addStep(text: String, icon: MaterialIconView, view: Node) {
        val bttn = Button("",icon)
        val tempVBox = vbox {
            add(bttn)
            add(label(text))
        }
        steps.add(tempVBox)
        views.add(view)
    }
}

fun Pane.progressstepper(init: ProgressStepper.() -> Unit): ProgressStepper {
    val ps = ProgressStepper()
    ps.init()
    return ps
}