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
    //    val selectedTakeProperty = bind { model.selectedTakeProperty}
    var selectedTake: Node by property(TakeCard(232.0, 120.0, TakeCardViewModel(TakeCardModel(model.takes[0]))))
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

    var draggingShadow: Node by property (Rectangle())
    var draggingShadowProperty = getProperty(ViewTakesViewModel::draggingShadow)


    var tempId: Int = 0
    var tempTargetNode: Node = VBox()
    var tempTakeCardTarget : Node = VBox()
    lateinit var tempTakeCard: TakeCard



    fun startDrag(evt: MouseEvent) {
        if (comparingTake == false && draggingTake == false) {
           tempTargetNode = evt.target as Node
             tempTakeCardTarget = tempTargetNode.findParentOfType(TakeCard::class) as Node
             tempTakeCard = tempTargetNode.findParentOfType(TakeCard::class) as TakeCard

            if(tempTakeCardTarget != draggingShadow) {
                        draggingShadow = tempTakeCardTarget
                    }
                    takeItems.firstOrNull()
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

        if (selectedTake == null) {
            selectedTake = tempTakeCardTarget
            draggingTake = false

        } else {
            takeToCompare = tempTakeCardTarget
            comparingTake = true
            draggingTake = false

        }
    }

    fun setTake() {
        takeToCompare.removeFromParent()
        selectedTake = takeToCompare
        comparingTake = false

    }

    fun cancelSetTake() {
        comparingTake = false
        takeToCompare.removeFromParent()
    }

}