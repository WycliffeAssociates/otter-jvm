package org.wycliffeassociates.otter.jvm.app.ui.viewtakes.ViewModel


import javafx.geometry.Point2D
import javafx.scene.Node
import javafx.scene.input.ClipboardContent
import javafx.scene.input.MouseEvent
import org.wycliffeassociates.otter.jvm.app.ui.viewtakes.Model.ViewTakesModel
import org.wycliffeassociates.otter.jvm.app.widgets.takecard.TakeCard
import tornadofx.*

class ViewTakesViewModel : ViewModel() {
    val model = ViewTakesModel()
    //    val selectedTakeProperty = bind { model.selectedTakeProperty}
    var selectedTake: Node by property()
    var selectedTakeProperty = getProperty(ViewTakesViewModel::selectedTake)

    //    var selectedTake: HBox by property()
//    var selectedTakeProperty = getProperty(ViewTakesViewModel:: selectedTake)
    val alternateTakes = model.alteranteTakes
    val alternateTakesProperty = bind { model.alternateTakesProperty }
    var takeItems = mutableListOf<TakeCard>()

    var takeToCompare: Node by property()
    var takeToCompareProperty = getProperty(ViewTakesViewModel::takeToCompare)

    var comparingTake: Boolean by property(false)
    var comparingTakeProperty = getProperty(ViewTakesViewModel::comparingTake)

    var draggingTake: Boolean by property(false)
    var draggingTakeProperty = getProperty(ViewTakesViewModel::draggingTake)

    var dragEvt: MouseEvent by property()
    var dragEvtProperty = getProperty(ViewTakesViewModel::dragEvt)

    var selectedTakeId: Int by property(0)
    var selectedTakeIdProperty = getProperty(ViewTakesViewModel::selectedTakeId)

    var tempId: Int = 0


    fun startDrag(evt: MouseEvent) {
        if (comparingTake == false) {
            takeItems.filter {
                val mousePt: Point2D = it.sceneToLocal(evt.sceneX, evt.sceneY)
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
        if (comparingTake == false) {
            dragEvt = evt
        }
    }

    fun completeDrag(evt: MouseEvent) {
        draggingTake = false

        val targetNode = evt.target as Node
        val takeCardTarget = targetNode.findParentOfType(TakeCard::class) as Node
        val takeCard = targetNode.findParentOfType(TakeCard::class) as TakeCard
        if (selectedTake == null) {
            selectedTake = takeCardTarget
            selectedTakeId = takeCard.takeId
        } else {
            takeToCompare = takeCardTarget
            tempId = takeCard.takeId
            comparingTake = true
        }

    }

    fun setTake() {
        takeToCompare.removeFromParent()
        selectedTakeId = tempId
        selectedTake = takeToCompare
        comparingTake = false

    }

    fun cancelSetTake() {
        comparingTake = false
        takeToCompare.removeFromParent()
    }

}