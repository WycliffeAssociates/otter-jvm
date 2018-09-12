package org.wycliffeassociates.otter.jvm.app.widgets.card

import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import tornadofx.add
import tornadofx.box
import tornadofx.px
import tornadofx.style

class Badge(
        badgeText: String = ""
) : StackPane() {
    val badgeLabel = Label(badgeText)
    init {
        style {
            // Use the "shape" property to define arbitrary SVG background shape
            padding = box(10.px, 10.px)
            backgroundRadius += box(0.px, 10.px, 0.px, 10.px)
        }
        add(badgeLabel)
    }
}

fun badge(badgeText: String = "", init: Badge.() -> Unit): Badge {
    val badge = Badge(badgeText)
    badge.init()
    return badge
}