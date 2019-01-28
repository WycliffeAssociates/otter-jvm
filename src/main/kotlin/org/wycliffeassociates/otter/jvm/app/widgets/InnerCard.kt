package org.wycliffeassociates.otter.jvm.app.widgets

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.layout.VBox
import tornadofx.*

class InnerCard: VBox() {
    val titleProperty = SimpleStringProperty()
    var title by titleProperty

    val bodyTextProperty = SimpleStringProperty()
    var bodyText by bodyTextProperty

    val majorLabelProperty = SimpleStringProperty()
    var majorLabel by majorLabelProperty

    val minorLabelProperty = SimpleStringProperty()
    var minorLabel by minorLabelProperty


    init {
        importStylesheet<DefaultStyles>()

        alignment = Pos.BOTTOM_CENTER
        label(titleProperty){  }
        label(bodyTextProperty)
        label(majorLabelProperty)
        label(minorLabelProperty)
        progressbar(0.2) { addClass(DefaultStyles.defaultCardProgressBar) }
        addClass(DefaultStyles.defaultInnerCard)

        //graphic either add it or set as background for this node
        //title
        //body label
        //major label
        //minor label
        //progressbar
    }
}

fun innercard(init: InnerCard.() -> Unit ={}): InnerCard {
    val ic = InnerCard()
    ic.init()
    return ic
}