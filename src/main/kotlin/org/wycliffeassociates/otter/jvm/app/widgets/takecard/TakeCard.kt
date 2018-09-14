package org.wycliffeassociates.otter.jvm.app.widgets.takecard

import javafx.scene.paint.Color
import org.wycliffeassociates.otter.jvm.app.UIColorsObject
import org.wycliffeassociates.otter.jvm.app.UIColorsObject.Colors
import org.wycliffeassociates.otter.jvm.app.widgets.audiocard.AudioCard
import org.wycliffeassociates.otter.jvm.app.widgets.card.Badge
import tornadofx.*
import tornadofx.Stylesheet.Companion.root

class TakeCard(
        width: Double, height: Double,
        viewModel: TakeCardViewModel
) : AudioCard(width, height, c(UIColorsObject.Colors["primary"]), viewModel) {
    init {
        setBadge(viewModel.newBadgeIsVisibleProperty.get())
        viewModel.newBadgeIsVisibleProperty.onChange { newValue ->
            setBadge(newValue)
        }
        with(root) {
            style {
                borderColor += box(c(Colors["neutral"]))
                borderWidth += box(1.0.px)
                borderRadius += box(10.0.px)
            }
        }
    }

    private fun setBadge(visible: Boolean) {
        badge = if (visible) Badge("NEW", accentColor, Color.WHITE) else null
    }

}