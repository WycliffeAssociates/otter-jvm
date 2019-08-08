package org.wycliffeassociates.otter.jvm.app.ui.takemanagement.view

import javafx.scene.Node
import javafx.scene.layout.FlowPane
import javafx.scene.layout.Priority
import javafx.stage.Stage
import org.wycliffeassociates.otter.common.data.workbook.Take
import org.wycliffeassociates.otter.jvm.app.theme.AppStyles
import org.wycliffeassociates.otter.jvm.app.ui.takemanagement.viewmodel.RecordableViewModel
import org.wycliffeassociates.otter.jvm.app.widgets.takecard.*
import org.wycliffeassociates.otter.jvm.utils.onChangeAndDoNow
import tornadofx.*
import kotlin.math.abs

class TakesFlowPane(
    primaryStage: Stage,
    recordableViewModel: RecordableViewModel,
    private val createTakeCard: (Take) -> TakeCard,
    private val createRecordCard: () -> Node,
    private val createBlankCard: () -> Node
): FlowPane() {
    inner class BlankCardNodeList {
        private val _nodes = mutableListOf<Node>()
        val nodes: MutableList<Node>
            get() {
                _nodes.removeAll { !children.contains(it) }
                return _nodes
            }
    }
    private val blankCardNodeList = BlankCardNodeList()

    private val blankCardNodes: MutableList<Node>
        get() = blankCardNodeList.nodes

    var updateAllCardsCount = 0
    var updateBlankCardsCount = 0
    var layoutChildrenCount = 0

    var isStageShown = false

    override fun layoutChildren() {
        super.layoutChildren()

        val bip = children.firstOrNull()?.boundsInParent

        // ****When isStageShown and layoutchildren is called, we are safe. The children have been given bounds.

        layoutChildrenCount++
        println("layout children called: $layoutChildrenCount")
        println("is stage shown = $isStageShown")
        println("bounds in parent " + if (bip == null) "NULL" else "NOT null")
        children.firstOrNull()?.boundsInParent

        if (isStageShown) {
        }
    }

    init {
        importStylesheet<RecordScriptureStyles>()
        importStylesheet<TakeCardStyles>()
        importStylesheet<AppStyles>()

        vgrow = Priority.ALWAYS
        addClass(RecordScriptureStyles.takeGrid)

        recordableViewModel.initialNumAlternateTakes.onChange { initialNumTakes ->
            if (initialNumTakes >= 0) {
                recordableViewModel.alternateTakes.sizeProperty.onChange {
                    if (isStageShown && it == initialNumTakes) {
                        updateAllCardsCount++
                        println("updateAllCards: $updateAllCardsCount")
                        updateAllCards(recordableViewModel.alternateTakes)
                    }
                }
//                recordableViewModel.alternateTakes.onChangeAndDoNow {
//                    if (isStageShown && it.size == initialNumAlternateTakes) {
//                        updateAllCardsCount++
//                        println("updateAllCards: $updateAllCardsCount")
//                        updateAllCards(recordableViewModel.alternateTakes)
//                    }
//                }
//                recordableViewModel.alternateTakes.onChange {
//                    if (isStageShown && recordableViewModel.alternateTakes.size == initialNumAlternateTakes) {
//                        updateAllCardsCount++
//                        println("updateAllCards: $updateAllCardsCount")
//                        updateAllCards(recordableViewModel.alternateTakes)
//                    }
//                }
            }
        }

        recordableViewModel.alternateTakes.onChangeAndDoNow {
            if (isStageShown) {
                updateAllCardsCount++
                println("updateAllCards: $updateAllCardsCount")
                updateAllCards(recordableViewModel.alternateTakes)
            }
        }

        primaryStage.setOnShown {
            isStageShown = true
//            updateAllCardsCount++
//            println("updateAllCards FROM SETONSHOWN: $updateAllCardsCount")
//            updateAllCards(alternateTakes)
////            updateBlankCards()
        }

        widthProperty().onChange {
            if (isStageShown) {
                updateBlankCardsCount++
                println("updateBlankCards: $updateBlankCardsCount")
                updateBlankCards()
            }
        }
    }

    private fun getMaxCardsInRow(): Int {
        // maxCardsInRow = computeMaxCardsInRow()
        val firstChildBounds = children.first().boundsInParent
//        if (firstChildBounds.width <= 0.0)
//            return

//        val cardWidth = firstChildBounds.width + hgap
//        val cardWidth = (children.first() as VBox).prefWidth + hgap
        val cardWidth = 348.0 + hgap // TODO: This value is hardcoded
//        if (cardWidth <= 0.0)
//            return

        val startX = firstChildBounds.minX
        val margin = startX - boundsInParent.minX

        // We need to account for the fact that the hgap exists BETWEEN cards, so there will be one fewer
        // hgaps than there are cards
        val availableLength = boundsInParent.width - 2*margin + hgap

        println("availableLength = $availableLength")

        return Math.floor((availableLength) / cardWidth).toInt()
    }

    private fun getNonBlankChildren() = children.filter { !blankCardNodes.contains(it) }

    fun updateBlankCards() {
//    private fun updateBlankCards() {
        if (boundsInParent.width <= 0.0)
            return

        val maxCardsInRow = getMaxCardsInRow()
//        val maxCardsInRow = 5
//        val numExcessCards = children.size % maxCardsInRow // TODO: This assumes all children are same size
        val numExcessCards = getNonBlankChildren().size % maxCardsInRow // TODO: This assumes all children are same size
        val numCardsInLastRow = if (numExcessCards == 0) maxCardsInRow else numExcessCards
        val numBlanksWanted = maxCardsInRow - numCardsInLastRow

//        val lastChild = children
//            .last { !blankCardNodes.contains(it) }
//        val availableSpace = boundsInParent.maxX - lastChild.boundsInParent.maxX
//
//        // Use the last child to estimate the width of adding a blank card
//        val cardWidth = lastChild.boundsInParent.width + hgap
//
//        val numBlanksWanted =  Math.floor(availableSpace / cardWidth).toInt()

        addOrRemoveBlankCards(numBlanksWanted)
    }

    private fun addOrRemoveBlankCards(numBlanksWanted: Int) {
//        println("addOrRemoveBlankCards called with $numBlanksWanted")
        val delta = numBlanksWanted - blankCardNodes.size
//        println("delta = $delta")
        if (delta > 0) {
            addBlankCards(delta)
        } else {
            removeBlankCards(abs(delta))
        }
    }

    private fun addBlankCards(num: Int) {
        for (i in 0 until num) {
            val blankCard = createBlankCard()

//            val oldMaxX = children.last().boundsInParent.maxX

            add(blankCard)
//            if (blankCard.boundsInParent.maxX < oldMaxX) {
//                // The blank card has wrapped to a new row, so we should remove it.
//                children.remove(blankCard)
//                return
//            }
            blankCardNodes.add(blankCard)
        }
    }

    private fun removeBlankCards(num: Int) {
        for (i in 0 until num) {
            println("Removing i = $i")
            val blankCard = blankCardNodes.removeAt(blankCardNodes.lastIndex)
            children.remove(blankCard)
        }
    }

    private fun updateAllCards(list: List<Take>) {
        clear()
        blankCardNodes.clear()

        add(createRecordCard())
        list
            .sortedBy { take -> take.number }
            .map { take -> createTakeCard(take) }
            .forEach { add(it) }

        updateBlankCards()
    }
}