package org.wycliffeassociates.otter.jvm.resourcecardpage.styles

import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
import tornadofx.*

typealias LinearU = Dimension<Dimension.LinearUnits>

class ResourceListStyles : Stylesheet() {

    companion object {
        val resourceGroupCard by cssclass()
        val resourceGroupList by cssclass()
    }

    init {
        resourceGroupCard {
            spacing = 10.px // VBox spacing
            padding = box(15.px)
            backgroundColor += AppTheme.colors.white
            effect = DropShadow(2.0, 2.0, 4.0, AppTheme.colors.lightBackground)
            backgroundRadius += box(5.px) // No border, so background needs to be rounded
            label {
                fontWeight = FontWeight.BOLD
            }
        }

        resourceGroupList {
            borderColor += box(Color.TRANSPARENT) // Necessary for border under status bar banner to stay visible
            padding = box(0.px, 0.px, 0.px, 80.px) // Left "margin"
            scrollBar {
                +margin(0.px, 0.px, 0.px, 80.px) // Margin between scrollbar and right side of cards
            }

            listCell {
                // Add space between the cards (top margin)
                // But need to make the "margin" at least as large as the dropshadow offsets
                +margin(30.px, 4.px, 0.px, 0.px)
            }
        }
    }

    private fun margin(top: LinearU, right: LinearU, bottom: LinearU, left: LinearU) = mixin {
        padding = box(top, right, bottom, left)
        backgroundInsets += box(top, right, bottom, left)
    }
}