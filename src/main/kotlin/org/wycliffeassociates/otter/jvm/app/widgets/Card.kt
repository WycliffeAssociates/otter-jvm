package org.wycliffeassociates.otter.jvm.app.widgets

import javafx.scene.paint.Color
import tornadofx.*

class Card: Fragment() {
    override val root = vbox{}

    init {
        add(CardFront().apply {
            style{
                backgroundColor += Color.WHITE
                prefHeight = 200.px
                prefWidth = 165.px
            }
        })
        //front of card
        //back of card... eventually
    }
}