package widgets

import javafx.scene.effect.DropShadow
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.Font
import recources.UIColors
import tornadofx.*

/**
 * This class is the style sheet for the language search
 * drop-downs for target and source languages
 *
 * Each is specified by it's color
 */

class LanguageSelectionStyle : Stylesheet() {

    companion object {
        val targetLanguageSelector by cssclass()
        val sourceLanguageSelector by cssclass()
        val targetChip by cssclass()
        val sourceChip by cssclass()
    }

    private val targetColor = c(UIColors.UI_PRIMARY)
    private val sourceColor = c(UIColors.UI_SECONDARY)

    init {

        targetLanguageSelector {

            borderColor = multi(box(targetColor))
            focusColor = targetColor
            faintFocusColor = Color.TRANSPARENT

            s(arrowButton) {
                backgroundColor = multi(Color.TRANSPARENT)
            }

            s(listView) {
                maxHeight = 125.px
            }

        }

        targetChip {

            and(hover) {
                effect = DropShadow(5.0, targetColor)
            }

            s(button) {
                backgroundColor = multi(Color.TRANSPARENT)
            }

        }

        sourceLanguageSelector {

            borderColor = multi(box(sourceColor))
            focusColor = sourceColor
            faintFocusColor = Color.TRANSPARENT

            s(arrowButton) {
                backgroundColor = multi(Color.TRANSPARENT)
            }

            s(listView) {
                maxHeight = 125.px
            }

        }

        sourceChip {

            and(hover) {
                effect = DropShadow(5.0, sourceColor)
            }

            s(button) {
                backgroundColor = multi(Color.TRANSPARENT)
            }

        }

    }

}