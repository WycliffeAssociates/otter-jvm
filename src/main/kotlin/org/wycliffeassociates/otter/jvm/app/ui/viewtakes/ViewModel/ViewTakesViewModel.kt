package org.wycliffeassociates.otter.jvm.app.ui.viewtakes.ViewModel

import javafx.beans.property.Property
import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.input.DragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.shape.Rectangle
import org.wycliffeassociates.otter.jvm.app.ui.viewtakes.Model.Take
import org.wycliffeassociates.otter.jvm.app.ui.viewtakes.Model.ViewTakesModel
import tornadofx.*

class ViewTakesViewModel : ViewModel(){
    val model = ViewTakesModel()
    val selectedTakeProperty = bind { model.selectedTakeProperty}
    val alternateTakesProperty = bind {model.alternateTakesProperty}
    var takeItems = mutableListOf<Node>()
    var takeToCompare: Boolean by property (false)
    var takeToCompareProperty = getProperty(ViewTakesViewModel::takeToCompare)
    var draggingTake: Boolean by property(false)
    var draggingTakeProperty = getProperty(ViewTakesViewModel::draggingTake)
    var dragEvt: MouseEvent by property()
    var dragEvtProperty = getProperty(ViewTakesViewModel::dragEvt)


    fun startDrag(evt: MouseEvent) {
        if(takeToCompare == false) {
            takeItems.filter {
                val mousePt: Point2D = it.sceneToLocal(evt.sceneX, evt.sceneY)
                println(mousePt)
                it.contains(mousePt)
            }
                    .firstOrNull()
                    .apply {
                        draggingTake = true
                    }
        }
    }

    fun cancelDrag(evt: MouseEvent) {
        draggingTake = false
    }

    fun animateDrag(evt: MouseEvent) {
        if(takeToCompare == false) {
            dragEvt = evt
        }
    }

    fun completeDrag(evt: MouseEvent) {
        takeToCompare = true
        draggingTake = false
    }

    fun setTake() {
        takeToCompare = false
    }

}