package org.wycliffeassociates.otter.jvm.app.ui.viewtakes.ViewModel


import javafx.scene.Node
import javafx.scene.input.MouseEvent
import javafx.scene.layout.VBox
import javafx.scene.shape.Rectangle
import org.wycliffeassociates.otter.jvm.app.ui.viewtakes.Model.ViewTakesModel
import org.wycliffeassociates.otter.jvm.app.widgets.takecard.TakeCard
import org.wycliffeassociates.otter.jvm.app.widgets.takecard.TakeCardModel
import org.wycliffeassociates.otter.jvm.app.widgets.takecard.TakeCardViewModel
import tornadofx.*

class ViewTakesViewModel : ViewModel() {
    val model = ViewTakesModel()
    var selectedTakeProperty = bind {model.selectedTakeProperty}

    val alternateTakesProperty = bind { model.alternateTakesProperty }

    var takeItems = mutableListOf<TakeCard>()
    var takeToCompareProperty = bind{model.takeToCompareProperty}
    var comparingTakeProperty = bind {model.comparingTakeProperty}
    var draggingTakeProperty = bind {model.draggingTakeProperty}

    var mousePosition : DoubleArray by property(doubleArrayOf(0.0,0.0))
    var mousePositionProperty = getProperty(ViewTakesViewModel::mousePosition)

    var draggingShadow: Node by property (Rectangle())
    var draggingShadowProperty = getProperty(ViewTakesViewModel::draggingShadow)


    var tempId: Int = 0
    var tempTargetNode: Node = VBox()
    var tempTakeCardTarget : Node = VBox()
    lateinit var tempTakeCard: TakeCard



    fun startDrag(evt: MouseEvent) {
        if (model.comparingTake == false && model.draggingTake == false) {
           tempTargetNode = evt.target as Node
             tempTakeCardTarget = tempTargetNode.findParentOfType(TakeCard::class) as Node
             tempTakeCard = tempTargetNode.findParentOfType(TakeCard::class) as TakeCard

            if(tempTakeCardTarget != draggingShadow) {
                        draggingShadow = tempTakeCardTarget
                    }
                    takeItems.firstOrNull()
                    .apply {
                        model.draggingTake = true
                    }
        }
    }

    fun cancelDrag(evt: MouseEvent) {
        model.draggingTake = false
    }

    fun animateDrag(evt: MouseEvent) {
        if (model.comparingTake == false) {
            mousePosition[0]= evt.sceneX
            mousePosition[1] = evt.sceneY
        }
    }

    fun completeDrag(evt: MouseEvent) {

        if (model.selectedTake == null) {
            model.selectedTake = tempTakeCardTarget
            model.draggingTake = false

        } else {
            model.takeToCompare = tempTakeCardTarget
            model.comparingTake = true
            model.draggingTake = false

        }
    }

    fun setTake() {
        model.takeToCompare.removeFromParent()
        model.selectedTake = model.takeToCompare
        model.comparingTake = false

    }

    fun cancelSetTake() {
        model.comparingTake = false
        model.takeToCompare.removeFromParent()
    }

}