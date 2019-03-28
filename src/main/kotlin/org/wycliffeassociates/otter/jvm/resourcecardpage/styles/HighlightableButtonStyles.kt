package org.wycliffeassociates.otter.jvm.resourcecardpage.styles

import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import javafx.scene.paint.Color
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.text.FontWeight
import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
import tornadofx.*

class HighlightableButtonStyles : Stylesheet() {

    companion object {
        val hButton by cssclass()
        val hButtonDefault by cssclass()
        val hButtonFocused by cssclass()
        val gridIcon by cssclass()
    }

    init {
        val hButtonFocusedMixin = mixin {
            textFill = AppTheme.colors.white
            backgroundColor += buttonLinearGradient(
                    AppTheme.colors.appLightOrange,
                    AppTheme.colors.appOrange
            )
            gridIcon {
                fill = AppTheme.colors.white
            }
        }

        // TODO: This shares a lot with DefaultStyles.defaultCardButton
        hButton {
            alignment = Pos.CENTER
            maxHeight = 40.px
            borderRadius += box(5.0.px)
            cursor = Cursor.HAND
            fontSize = 16.px
            fontWeight = FontWeight.BOLD
        }

        hButtonDefault {
            textFill = AppTheme.colors.appOrange
            backgroundColor += AppTheme.colors.white
            gridIcon {
                fill = AppTheme.colors.appOrange // TODO don't hardcode colors (also for hover)
            }

            and(hover) {
                +hButtonFocusedMixin
            }
        }

        hButtonFocused {
            +hButtonFocusedMixin
        }
    }

    private fun buttonLinearGradient(startColor: Color, endColor: Color): LinearGradient =
            LinearGradient(
                    0.0,
                    0.0,
                    0.0,
                    1.0,
                    true,
                    CycleMethod.NO_CYCLE,
                    Stop(0.0, startColor),
                    Stop(1.0, endColor)
            )
}