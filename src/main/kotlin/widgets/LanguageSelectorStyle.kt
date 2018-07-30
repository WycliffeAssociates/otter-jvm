package widgets

import javafx.geometry.Pos
import javafx.scene.paint.Color
import tornadofx.*

/**
 * This class is the style sheet for the Selector widget
 * combobox and for the chips of the selected items.
 *
 * Each is specified by it's color
 */

class LanguageSelectorStyle : Stylesheet() {

    companion object {
        val comboBoxSelector by csselement("ComboBoxSelector")
        val chip by csselement("Chip")
        val rectangle by csselement("Rectangle")
    }

    init {

        s(comboBoxSelector) {
            faintFocusColor = Color.TRANSPARENT

            s(arrowButton) {
                backgroundColor = multi(Color.TRANSPARENT)
            }

            s(listView) {
                maxHeight = 125.px
            }
        }

        s(chip) {
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
                padding = box(10.px, 10.px, 5.px, 10.px)

                and(hover) {
                    opacity = 1.0
                }
            }

            s(label) {
                alignment = Pos.CENTER_LEFT
                padding = box(10.px, 0.px, 10.px, 20.px)
            }
        }

    }
}