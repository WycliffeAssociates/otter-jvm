package org.wycliffeassociates.otter.jvm.app.ui.styles

import javafx.scene.Cursor
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import org.wycliffeassociates.otter.jvm.app.UIColorsObject
import tornadofx.*

class AppStyles : Stylesheet() {

    companion object {
        val datagridStyle by cssclass()
        val addProjectButton by cssclass()
        val refreshButton by cssclass()
        val projectCard by cssclass()
        val projectGraphicContainer by cssclass()
    }

    init {
        datagridStyle {
            cell {
                backgroundColor += Color.TRANSPARENT
            }
            effect = DropShadow(8.0, 0.0, 0.0, c(UIColorsObject.Colors["dropShadow"]))
            backgroundRadius += box(10.0.px)
            borderRadius += box(10.0.px)
            cellHeight = 250.0.px
            cellWidth = 232.0.px
            horizontalCellSpacing = 10.0.px
            padding = box(0.px, 30.px)
        }

        addProjectButton {
            backgroundRadius += box(25.px)
            borderRadius += box(25.px)
            backgroundColor += c(UIColorsObject.Colors["primary"])
            minHeight = 50.px
            minWidth = 50.px
            maxHeight = 50.px
            maxWidth = 50.px
            unsafe("-jfx-button-type", raw("RAISED"))
            child("*") {
                fill = c(UIColorsObject.Colors["base"])
            }
        }

        refreshButton {
            prefHeight = 40.0.px
            //backgroundColor += c(UIColorsObject.Colors["primary"])
            unsafe("-jfx-button-type", raw("FLAT"))
            child("*") {
                fill = c(UIColorsObject.Colors["primary"])
            }
        }

        projectCard {
            backgroundColor += c(UIColorsObject.Colors["base"])
            padding = box(10.px)
            backgroundRadius += box(10.px)
            spacing = 10.px
            projectGraphicContainer {
                backgroundRadius += box(10.px)
                backgroundColor += c(UIColorsObject.Colors["baseLight"])
            }
            label {
                textFill = Color.BLACK
            }
            s(".jfx-button") {
                minHeight = 40.0.px
                maxWidth = Double.MAX_VALUE.px
                backgroundColor += c(UIColorsObject.Colors["primary"])
                textFill = c(UIColorsObject.Colors["base"])
                cursor = Cursor.HAND
                fontSize = (16.0.px)
                fontWeight = FontWeight.BLACK
            }
        }
    }

}
