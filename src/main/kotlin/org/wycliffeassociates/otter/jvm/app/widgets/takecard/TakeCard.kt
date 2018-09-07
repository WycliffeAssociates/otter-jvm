package org.wycliffeassociates.otter.jvm.app.widgets.takecard

import javafx.scene.paint.Color
import org.wycliffeassociates.otter.jvm.app.UIColorsObject
import org.wycliffeassociates.otter.jvm.app.widgets.audiocard.AudioCard
import org.wycliffeassociates.otter.jvm.app.widgets.card.Badge
import tornadofx.c
import tornadofx.get
import tornadofx.onChange

class TakeCard(
        width: Double, height: Double,
        viewModel: TakeCardViewModel
) : AudioCard(width, height, c(UIColorsObject.Colors["primary"]), viewModel) {

    init {
        setBadge(viewModel.newBadgeIsVisibleProperty.get())
        viewModel.newBadgeIsVisibleProperty.onChange { newValue ->
            setBadge(newValue)
        }
    }

    private fun setBadge(visible: Boolean) {
        badge = if (visible) Badge("NEW", accentColor, Color.WHITE) else null
    }
}