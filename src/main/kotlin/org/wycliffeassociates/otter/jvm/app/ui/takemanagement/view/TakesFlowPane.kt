package org.wycliffeassociates.otter.jvm.app.ui.takemanagement.view

import javafx.scene.Node
import javafx.scene.effect.DropShadow
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

    var isStageShown = false

    override fun layoutChildren() {
        super.layoutChildren()
        if (isStageShown) {
            println("updateBlankCards called")
            updateBlankCards()
        }
    }

    init {
        importStylesheet<RecordScriptureStyles>()
        importStylesheet<TakeCardStyles>()
        importStylesheet<AppStyles>()

        vgrow = Priority.ALWAYS
        addClass(RecordScriptureStyles.takeGrid)

        recordableViewModel.alternateTakes.onChangeAndDoNow { alternateTakes ->
            updateAlternateTakeCards(alternateTakes)
        }

        primaryStage.setOnShown {
            isStageShown = true
        }
    }

    private fun getMaxCardsInRow(): Int {
        val firstChild = children.first()
        val firstChildBounds = firstChild.boundsInParent

        val dropShadowWidth = (firstChild.effect as? DropShadow)?.width ?: 0.0
        val cardWidth = firstChildBounds.width - dropShadowWidth + hgap

        val startX = firstChildBounds.minX
        val margin = startX - boundsInParent.minX
        val startMargin = startX - boundsInParent.minX

        var endX: Double
        var lastEndX = 0.0
        val nonBlankChildren = getNonBlankChildren()
        for (i in 0 until nonBlankChildren.size) {
            endX = nonBlankChildren[i].boundsInParent.maxX
            if (endX < lastEndX)
                break
            lastEndX = endX
        }
        val endMargin = boundsInParent.maxX - lastEndX

        // We need to account for the fact that the hgap exists BETWEEN cards, so there will be one fewer
        // hgaps than there are cards
//        val availableLength = boundsInParent.width - 2*margin + hgap
        val availableLength = boundsInParent.width - startMargin - endMargin + hgap

        return Math.floor((availableLength) / cardWidth).toInt()
    }

    private fun getNonBlankChildren() = children.filter { !blankCardNodes.contains(it) }

    private fun updateBlankCards() {
        val maxCardsInRow = getMaxCardsInRow()
        val numExcessCards = getNonBlankChildren().size % maxCardsInRow // TODO: This assumes all children are same size
        val numCardsInLastRow = if (numExcessCards == 0) maxCardsInRow else numExcessCards
        val numBlanksWanted = maxCardsInRow - numCardsInLastRow

        addOrRemoveBlankCards(numBlanksWanted)
    }

    private fun addOrRemoveBlankCards(numBlanksWanted: Int) {
        val delta = numBlanksWanted - blankCardNodes.size
        if (delta > 0) {
            addBlankCards(delta)
        } else {
            removeBlankCards(abs(delta))
        }
    }

    private fun addBlankCards(num: Int) {
        for (i in 0 until num) {
            val blankCard = createBlankCard()
            add(blankCard)
            blankCardNodes.add(blankCard)
        }
    }

    private fun removeBlankCards(num: Int) {
        for (i in 0 until num) {
            val blankCard = blankCardNodes.removeAt(blankCardNodes.lastIndex)
            children.remove(blankCard)
        }
    }

    private fun updateAlternateTakeCards(list: List<Take>) {
        clear()
        blankCardNodes.clear()

        add(createRecordCard())
        list
            .sortedBy { take -> take.number }
            .map { take -> createTakeCard(take) }
            .forEach { add(it) }
    }
}