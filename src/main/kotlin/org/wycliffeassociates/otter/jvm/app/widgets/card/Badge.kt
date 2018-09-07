package org.wycliffeassociates.otter.jvm.app.widgets.card

import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import tornadofx.add
import tornadofx.box
import tornadofx.px
import tornadofx.style

class Badge(
        badgeText: String = "",
        badgeColor: Color = Color.BLACK,
        badgeTextColor: Color = Color.WHITE
) : StackPane() {
    private val badgeLabel = Label(badgeText)
    init {
        style {
            // Use the "shape" property to define arbitrary SVG background shape
            backgroundColor += badgeColor
            padding = box(10.px, 10.px)
            backgroundRadius += box(0.px, 10.px, 0.px, 10.px)
        }
        badgeLabel.style {
            textFill = badgeTextColor
        }
        add(badgeLabel)
    }
}