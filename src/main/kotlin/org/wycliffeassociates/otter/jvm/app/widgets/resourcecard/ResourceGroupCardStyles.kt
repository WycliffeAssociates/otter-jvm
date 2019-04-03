package org.wycliffeassociates.otter.jvm.app.widgets.resourcecard

import javafx.scene.effect.DropShadow
import javafx.scene.text.FontWeight
import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
import tornadofx.*

class ResourceGroupCardStyles : Stylesheet() {

    companion object {
        val resourceGroupCard by cssclass()
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
    }
}