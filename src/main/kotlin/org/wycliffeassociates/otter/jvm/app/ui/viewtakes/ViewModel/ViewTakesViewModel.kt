package org.wycliffeassociates.otter.jvm.app.ui.viewtakes.ViewModel

import javafx.beans.property.Property
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import org.wycliffeassociates.otter.jvm.app.ui.viewtakes.Model.Take
import org.wycliffeassociates.otter.jvm.app.ui.viewtakes.Model.ViewTakesModel
import tornadofx.*

class ViewTakesViewModel(private val model : ViewTakesModel) : ViewModel(){

    val selectedTakeProperty = bind { model.selectedTakeProperty}
    val alternateTakesProperty = bind {model.alternateTakesProperty}
    var takeToCompare: Property<Take>? = null
    var takeItems = mutableListOf<Node>()
    var draggingTake = FX.observable<FX.Companion, Boolean>()

    fun startDrag(evt: MouseEvent) {
        takeItems.filter {
            val mousePt: Point2D = it.sceneToLocal(evt.sceneX, evt.sceneY)
            it.contains(mousePt)
        }
                .firstOrNull()
                .apply{
                    draggingTake.setValue(true)
                }
    }

    fun cancelDrag(evt: MouseEvent) {
        draggingTake.setValue(false)
    }

    fun animateDrag(evt: MouseEvent) {

    }

    fun completeDrag(evt: MouseEvent) {
        draggingTake.setValue(false)
    }

}