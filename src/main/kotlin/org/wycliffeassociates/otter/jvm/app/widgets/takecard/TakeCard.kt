package org.wycliffeassociates.otter.jvm.app.widgets.takecard

import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import org.wycliffeassociates.otter.jvm.app.UIColorsObject.Colors
import org.wycliffeassociates.otter.jvm.app.widgets.audiocard.AudioCard
import org.wycliffeassociates.otter.jvm.app.widgets.card.Badge
import tornadofx.*

class TakeCard(
        viewModel: TakeCardViewModel
) : AudioCard(viewModel) {

    private val newBadge = Badge("NEW")

    init {
        this.setMaxSize(300.0, 150.0)

        with(playButton) {
            graphic.style(true) {
                fill = c(Colors["primary"])
            }
        }
        with(newBadge) {
            style(true) {
                backgroundColor += c(Colors["primary"])
            }
            badgeLabel.style {
                textFill = Color.WHITE
            }
        }
        setBadge(viewModel.newBadgeIsVisibleProperty.get())
        viewModel.newBadgeIsVisibleProperty.onChange { newValue ->
            setBadge(newValue)
        }
    }

    private fun setBadge(visible: Boolean) {
        badge = if (visible) newBadge else null
    }
}

fun Pane.takecard(viewModel: TakeCardViewModel, init: TakeCard.() -> Unit): TakeCard {
    val takeCard = TakeCard(viewModel)
    takeCard.init()
    add(takeCard)
    return takeCard
}