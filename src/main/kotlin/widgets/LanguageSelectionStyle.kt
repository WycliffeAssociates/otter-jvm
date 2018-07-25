package widgets

import javafx.geometry.Pos
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
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

        val rectangle by csselement("Rectangle")
        val stackpane by csselement("StackPane")

    }

    private val targetColor = c(UIColors.UI_PRIMARY)
    private val sourceColor = c(UIColors.UI_SECONDARY)

    init {

        targetLanguageSelector {

            s(comboBox) {
                borderColor = multi(box(targetColor))
                focusColor = targetColor
                faintFocusColor = Color.TRANSPARENT
            }

            s(arrowButton) {
                backgroundColor = multi(Color.TRANSPARENT)
            }

            s(listView) {
                maxHeight = 125.px
            }

        }

        targetChip {

            s(stackpane) {

                alignment = Pos.CENTER_LEFT
                prefHeight = 25.px

                s(rectangle) {
                    arcWidth = 30.px
                    arcHeight = 30.px
                    prefHeight = 25.px
                }

                s(button) {
                    backgroundColor = multi(Color.TRANSPARENT)
                    opacity = 0.65
                    alignment = Pos.CENTER_RIGHT
                    padding = box(12.px, 10.px, 5.px, 10.px)

                    and(hover) {
                        opacity = 1.0
                        fontWeight = FontWeight.BOLD
                    }


                }

                s(label) {
                    alignment = Pos.CENTER_LEFT
                    padding = box(10.px, 0.px, 10.px, 20.px)
                }

                and(hover) {
                    effect = DropShadow(5.0, targetColor)
                }
            }
        }

        sourceLanguageSelector {

            s(comboBox) {
                borderColor = multi(box(sourceColor))
                focusColor = sourceColor
                faintFocusColor = Color.TRANSPARENT
            }

            s(arrowButton) {
                backgroundColor = multi(Color.TRANSPARENT)
            }

            s(listView) {
                maxHeight = 125.px
            }
        }

        sourceChip {

            s(stackpane) {

                alignment = Pos.CENTER_LEFT
                prefHeight = 25.px

                s(rectangle) {
                    arcWidth = 30.px
                    arcHeight = 30.px
                    prefHeight = 25.px
                }

                s(button) {
                    backgroundColor = multi(Color.TRANSPARENT)
                    opacity = 0.65
                    alignment = Pos.CENTER_RIGHT
                    padding = box(12.px, 10.px, 5.px, 10.px)

                    and(hover) {
                        opacity = 1.0
                        fontWeight = FontWeight.BOLD
                    }


                }

                s(label) {
                    alignment = Pos.CENTER_LEFT
                    padding = box(10.px, 0.px, 10.px, 20.px)
                }

                and(hover) {
                    effect = DropShadow(5.0, sourceColor)
                }
            }
        }

    }

}