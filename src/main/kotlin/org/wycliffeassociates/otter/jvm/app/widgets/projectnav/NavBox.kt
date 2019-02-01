package org.wycliffeassociates.otter.jvm.app.widgets.projectnav

import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import org.wycliffeassociates.otter.jvm.app.widgets.card.InnerCard
import tornadofx.*
class NavBox(mainLabel: String? = null, graphic: Node? = null): StackPane() {

    val layerList = observableList<Node>()

    fun innercard(init: InnerCard.() -> Unit = {}): InnerCard {
        val ic = InnerCard()
        ic.init()
        addLayer(ic)
        return ic
    }

    fun addLayer(nextLayer: Node) {
        //add the layer that will be added on top the empty nav box
        layerList.setAll(nextLayer)
    }

    init {

        style {
            prefWidth = 150.px
            prefHeight = 150.px
            backgroundColor += c("#E6E8E9")
            borderWidth += box(2.0.px)
            borderColor += box(Color.GRAY)
            borderRadius += box(5.0.px)
            backgroundRadius += box(5.0.px)
            maxWidth = 180.0.px
            cursor = Cursor.HAND
        }
         vbox(10) {
             vgrow = Priority.ALWAYS
             hgrow = Priority.ALWAYS
             alignment = Pos.CENTER
             if(graphic != null) {
                 add(graphic)
             }

             if(mainLabel != null) {
                 label(mainLabel)
             }

         }

        layerList.onChange {
            it.list.map {
                add(it)
            }
        }
    }
}

fun navbox(mainLabel: String? = null, graphic: Node? = null, init: NavBox.()-> Unit = {}): NavBox {
    val nb = NavBox(mainLabel, graphic)
    nb.init()
    return nb
}