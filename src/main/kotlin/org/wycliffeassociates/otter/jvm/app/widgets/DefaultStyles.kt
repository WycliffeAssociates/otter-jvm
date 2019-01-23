package org.wycliffeassociates.otter.jvm.app.widgets

import javafx.scene.layout.BackgroundPosition
import javafx.scene.layout.BackgroundRepeat
import javafx.scene.layout.BackgroundSize
import javafx.scene.paint.Color
import tornadofx.*
import java.net.URI

class DefaultStyles: Stylesheet() {
    private val defaultRed = c("#CC4141")
    private val defaultWhite = c("#FFFF")
    companion object {
        val defaultBaseTop by cssclass()
        val baseBottom by cssclass()
        val defaultInnerCard by cssclass()
    }

    init {
        defaultBaseTop {
            prefWidth = 180.px
            prefHeight = 70.px
            maxHeight = 70.px
            backgroundRadius += box(0.0.px, 0.0.px, 25.0.px, 25.0.px)
            backgroundColor +=defaultRed
        }

        baseBottom {
            backgroundColor += defaultWhite
        }

        defaultInnerCard {
            backgroundColor += Color.LIGHTGRAY
            borderColor += box(Color.ALICEBLUE)
            borderWidth += box(1.0.px)
            backgroundImage += URI("/images/placeholder-image.png")
            backgroundRepeat += Pair(BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT)
            backgroundPosition += BackgroundPosition.CENTER
            backgroundSize += BackgroundSize(90.0,
                    90.0,
                    true,
                    true,
                    true,
                    true)
        }
    }
}