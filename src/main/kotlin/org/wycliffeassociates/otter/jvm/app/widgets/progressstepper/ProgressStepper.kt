package org.wycliffeassociates.otter.jvm.app.widgets.progressstepper

import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import tornadofx.*

class ProgressStepper() : HBox() {
    var steps = observableList<Step>()

    val fillColorProperty = SimpleObjectProperty<Color>()
    var fillColor by fillColorProperty

    init {
        alignment = Pos.CENTER
        spacing = 16.0
        steps.onChange {
            it.list.forEach {
                add(it)
            }
        }
    }

    fun step(separator: Boolean = true, init: Step.() -> Unit = {}): Step {
        val st = Step(separator)
        st.init()
        steps.add(st)
        return st
    }
}

fun progressstepper(init: ProgressStepper.() -> Unit = {}): ProgressStepper {
    val ps = ProgressStepper()
    ps.init()
    return ps
}