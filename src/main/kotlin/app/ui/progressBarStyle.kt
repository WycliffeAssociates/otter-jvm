package app.ui

import javafx.geometry.Pos
import javafx.scene.paint.Color
import javafx.scene.text.Font
import tornadofx.*

class progressBarCSS : Stylesheet() {
    companion object {
        val progressBarStyle by cssclass()
    }
    init {
        s(label) {
            font = Font.font("NotoSans-Black")
        }

        /**
         * Styles the progress bar.
         */
        progressBarStyle {
            alignment = Pos.TOP_CENTER
            backgroundRadius += box(50.px)
            minWidth = 150.px
            accentColor = c("#E56060")
        }
        s(bar, track) {
            backgroundRadius += box(50.px)
        }
        s(track) {
            backgroundColor += Color.TRANSPARENT
        }
    }
}