package org.wycliffeassociates.otter.jvm.app.widgets

import javafx.beans.property.SimpleStringProperty
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import tornadofx.*

class Card: VBox() {
//    override val root = vbox{}
//    val cardtitleProperty = SimpleStringProperty()
//    var cardtitle by cardtitleProperty
//
//    val cardbodyTextProperty = SimpleStringProperty()
//    var cardBodyText by cardbodyTextProperty
//
//    val cardmajorLabelProperty = SimpleStringProperty()
//    var cardMajorLabel by cardmajorLabelProperty
//
//    val cardminorLabelProperty = SimpleStringProperty()
//    var cardMinorLabel by cardminorLabelProperty
//
//    val cardFrontBackground: Color? = null
//
//    var cardFront: CardFront by singleAssign()

    fun cardfront(init: CardFront.() -> Unit = {}): CardFront {
        val cf = CardFront()
        cf.init()
        return cf
    }

    init {

//        add(cardFront)
        //front of card
//        add(cardfront {
//            cardFrontMajorLabelProperty.bind(cardmajorLabelProperty)
//            cardFrontMinorLabelProperty.bind(cardminorLabelProperty)
//            cardFrontButton.apply {
//                action{
//
//                }
//            }
//            addClass(DefaultStyles.defaultCard)
//        })
//        // todo add back of card...
    }
}

fun card(init: Card.() -> Unit ={}): Card {
    val cd = Card()
    cd.init()
    return cd
}