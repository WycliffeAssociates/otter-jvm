package org.wycliffeassociates.otter.jvm.app.widgets

import javafx.geometry.Pos
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import tornadofx.*

class InnerCard: VBox() {
    init {
        importStylesheet<DefaultStyles>()

        alignment = Pos.CENTER
        label("Chapter"){  }
        label("09")
        label("Revelation")
        label("ENG")
        progressbar(0.2) {  }
        addClass(DefaultStyles.defaultInnerCard)

        //graphic either add it or set as background for this node
        //title
        //body label
        //major label
        //minor label
        //progressbar
    }
}