package org.wycliffeassociates.otter.jvm.app.ui.takemanagement.view

import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
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
) : FlowPane() {
    private val blankCardNodes = mutableListOf<Node>()

    private val isStageShownProperty = SimpleBooleanProperty(false)
    private var alternateTakesJustUpdated = false

    override fun layoutChildren() {
        super.layoutChildren()
        if (isStageShownProperty.value) {
            callUpdateBlankCards()
        } else {
            isStageShownProperty.onChangeOnce {
                callUpdateBlankCards()
            }
        }
    }

    init {
        importStylesheet<RecordScriptureStyles>()
        importStylesheet<TakeCardStyles>()
        importStylesheet<AppStyles>()

        vgrow = Priority.ALWAYS
        addClass(RecordScriptureStyles.takeGrid)

        primaryStage.setOnShown {
            isStageShownProperty.set(true)
        }

        recordableViewModel.alternateTakes.onChangeAndDoNow { alternateTakes ->
            alternateTakesJustUpdated = true
            updateAlternateTakeCards(alternateTakes)
        }
    }

    private fun callUpdateBlankCards() {
        // If the alternate takes have just been loaded, we need to update the blank cards using Platform.runLater,
        // otherwise the blank cards will not appear if there are no alternate takes.
        // However, we don't always want to call Platform.runLater since it may cause unwanted blanks to flicker
        // in a new row when the window is getting resized.
        if (alternateTakesJustUpdated) {
            Platform.runLater {
                updateBlankCards()
            }
            alternateTakesJustUpdated = false
        } else {
            updateBlankCards()
        }
    }

    private fun getMaxCardsInRow(): Int {
        val firstChild = children.first()
        val firstChildBounds = firstChild.boundsInParent

        val dropShadowWidth = (firstChild.effect as? DropShadow)?.width ?: 0.0
        val cardWidth = firstChildBounds.width - dropShadowWidth + hgap

        val margin = firstChildBounds.minX
        // We need to account for the fact that the hgap exists BETWEEN cards, so there will be one fewer
        // hgaps than there are cards
        val availableLength = boundsInParent.width - 2 * margin + hgap

        // Calculate the maximum number of cards that can fit in a row
        val nonRoundedMax = availableLength / cardWidth
        val roundedMax = Math.floor(nonRoundedMax).toInt()

        // When there should only be one row, we need to take care that we do not add too many blank cards since they
        // can easily overflow to a second row. In this case, we should underestimate the amount of space we have.
        return if (getNonBlankChildren().size <= roundedMax && nonRoundedMax - roundedMax < 0.02) {
            roundedMax - 1
        } else roundedMax
    }

    private fun getNonBlankChildren() = children.filter { !blankCardNodes.contains(it) }

    private fun updateBlankCards() {
        val maxCardsInRow = getMaxCardsInRow()
        val numExcessCards = getNonBlankChildren().size % maxCardsInRow // This assumes all children are same size
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