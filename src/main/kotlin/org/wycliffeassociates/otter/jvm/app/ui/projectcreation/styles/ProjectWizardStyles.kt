package org.wycliffeassociates.otter.jvm.app.ui.projectcreation.styles

import javafx.scene.Cursor
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import org.wycliffeassociates.otter.jvm.app.UIColorsObject
import tornadofx.*


class ProjectWizardStyles : Stylesheet() {

    companion object {
        val selectedCard by cssclass()
        val unselectedCard by cssclass()
    }

    init {
        selectedCard {
            prefHeight = 364.0.px
            prefWidth = 364.0.px
            backgroundRadius += box(12.0.px)
            backgroundColor += c(UIColorsObject.Colors["primary"])
            textFill = c(UIColorsObject.Colors["base"])
            fontSize = 24.px
            effect = DropShadow(10.0, Color.GRAY)
            cursor = Cursor.HAND
        }

        unselectedCard {
            prefHeight = 364.0.px
            prefWidth = 364.0.px
            backgroundRadius += box(12.0.px)
            backgroundColor += c(UIColorsObject.Colors["base"])
            textFill = c(UIColorsObject.Colors["primary"])
            fontSize = 24.px
            effect = DropShadow(10.0, Color.GRAY)
            cursor = Cursor.HAND
        }
    }

}