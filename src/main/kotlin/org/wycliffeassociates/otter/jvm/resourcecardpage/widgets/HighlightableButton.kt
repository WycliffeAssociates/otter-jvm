package org.wycliffeassociates.otter.jvm.resourcecardpage.widgets

import com.jfoenix.controls.JFXButton
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.scene.Node
import org.wycliffeassociates.otter.jvm.resourcecardpage.styles.HighlightableButtonStyles
import tornadofx.FX.Companion.messages
import tornadofx.*

abstract class HighlightableButton(btnText: String, btnGraphic: Node) : JFXButton() {

    init {
        importStylesheet<HighlightableButtonStyles>()

        addClass(HighlightableButtonStyles.hButton)
        text = btnText
        graphic = btnGraphic
                .addClass(HighlightableButtonStyles.gridIcon)
        isDisableVisualFocus = true
    }
}

class NewTakeButton : HighlightableButton(
        messages["newTake"].toUpperCase(),
        MaterialIconView(MaterialIcon.MIC_NONE, "25px")
) {
    init {
        maxWidth = 500.0
        addClass(HighlightableButtonStyles.hButtonFocused)
    }
}

class ViewRecordingsButton : HighlightableButton(
        messages["viewRecordings"],
        MaterialIconView(MaterialIcon.APPS, "25px")
) {
    init {
        maxWidth = 250.0
        addClass(HighlightableButtonStyles.hButtonDefault)
    }
}