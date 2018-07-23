package widgets

import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.Font
import recources.UIColors
import tornadofx.*

/**
 * This class is the style sheet for the language search
 * drop-downs for target and source languages.
 *
 * Each is specified by its color.
 */

class LanguageSelectionStyle : Stylesheet() {

    companion object {
        val targetLanguageSelector by cssclass()
        val sourceLanguageSelector by cssclass()
        val makeItHoverPRIMARY by cssclass()
        val makeItHoverSECONDARY by cssclass()

        val bg by cssproperty<MultiValue<Paint>>("-fx-background-color")
        val targetColor = Color.valueOf(UIColors.UI_PRIMARY)
        val sourceColor = Color.valueOf(UIColors.UI_SECINDARY)
    }

    private val notoFont = Font.font("NotoSans-Black", 8.0)

    init {

        s(button) {
            bg.value += Color.TRANSPARENT
        }

        s(label) {
            font = notoFont
            fontSize = 10.pt
        }

        // PRIMARY styling for buttons (target)
        makeItHoverPRIMARY {
            and(hover) {
                effect =  DropShadow(5.0, targetColor)
            }
        }

        // SECONDARY styling for buttons (source)
        makeItHoverSECONDARY {
            and(hover) {
                effect =  DropShadow(5.0, sourceColor)
            }
        }

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

    }
}