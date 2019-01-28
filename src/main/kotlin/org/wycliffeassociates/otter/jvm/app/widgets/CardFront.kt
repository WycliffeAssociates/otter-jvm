package org.wycliffeassociates.otter.jvm.app.widgets

import com.jfoenix.controls.JFXButton
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import tornadofx.*

class CardFront: StackPane() {


    val cardBase: CardBase by singleAssign()
    val innerCard: InnerCard by singleAssign()
    val cardButton: Button by singleAssign()

    var cardFrontTitleProperty = SimpleStringProperty()
    var cardFrontBodyProperty = SimpleStringProperty()
    var cardFrontMajorLabelProperty = SimpleStringProperty()
    var cardFrontMinorLabelProperty = SimpleStringProperty()
    var cardFrontButton: Button by singleAssign()


    val defaultFill = c("#CC4141")
    val forwardArrow = MaterialIconView(MaterialIcon.ARROW_FORWARD,"20px")

    init {
        importStylesheet<DefaultStyles>()
//        forwardArrow.fill = defaultFill
////        add(cardbase {
////                style {
////                    backgroundColor += Color.RED
////                }
////        })
//        vbox(20) {
//            alignment = Pos.CENTER
//            vgrow = Priority.NEVER
//            style {
//                maxHeight = 220.0.px
//                padding = box(5.0.px)
//            }
//            add(innercard {
//                titleProperty.bind(cardFrontTitleProperty)
//                bodyTextProperty.bind(cardFrontBodyProperty)
//                majorLabelProperty.bind(cardFrontMajorLabelProperty)
//                minorLabelProperty.bind(cardFrontMinorLabelProperty)
//            })
//            cardFrontButton = JFXButton("Open", forwardArrow).apply{
//                addClass(DefaultStyles.defaultCardButton)
//            }
//            add(cardFrontButton)
//        }
    }
}

fun cardfront(init: CardFront.() -> Unit ={}): CardFront {
    val cf = CardFront()
    cf.init()
    return cf
}