package org.wycliffeassociates.otter.jvm.app.widgets.card

import javafx.scene.layout.*
import javafx.scene.paint.Color
import tornadofx.*

open class Card<T: Pane>(width: Double, height: Double, val content: T) : AnchorPane() {
    var badge: Badge? = null
        set(value) {
            if (value == null) {
                field?.removeFromParent()
            }
            field = value
            value?.let {
                setRightAnchor(value, 0.0)
                setTopAnchor(value, 0.0)
                add(value)
            }
        }

    init {
        style {
            backgroundColor += Color.WHITE
            backgroundRadius += box(10.px, 10.px)
        }
        content.style {
            padding = box(10.px)
        }
        setTopAnchor(content, 0.0)
        setBottomAnchor(content, 0.0)
        setLeftAnchor(content, 0.0)
        setRightAnchor(content, 0.0)
        add(content)
        this.setMaxSize(width, height)
    }
}