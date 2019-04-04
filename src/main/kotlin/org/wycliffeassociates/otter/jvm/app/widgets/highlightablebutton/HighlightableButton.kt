package org.wycliffeassociates.otter.jvm.app.widgets.highlightablebutton

import com.jfoenix.controls.JFXButton
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.paint.Color
import tornadofx.*

class HighlightableButton : JFXButton() {

    var primaryColor: Color = Color.BLACK
    var secondaryColor: Color = Color.WHITE
    var hasBorder: Boolean = false
    var isHighlighted: SimpleBooleanProperty = SimpleBooleanProperty(false)

    init {
        importStylesheet<HighlightableButtonStyles>()

        addClass(HighlightableButtonStyles.hButton)
        isDisableVisualFocus = true

        isHighlighted.onChange {
            applyColors()
        }
        hoverProperty().onChange {
            applyColors()
        }

        applyColors()
    }

    fun applyColors() {
        if (hoverProperty().get() || isHighlighted.get()) {
            doApplyColors(primaryColor, secondaryColor)
        } else {
            doApplyColors(secondaryColor, primaryColor)
        }
    }

    private fun doApplyColors(bgColor: Color, contentFillColor: Color) {
        style {
            backgroundColor += bgColor
            if(hasBorder) {
                borderColor += box(primaryColor)
            }
        }
        (graphic as? MaterialIconView)?.apply {
            fill = contentFillColor
        }
        textFill = contentFillColor
    }
}

fun highlightablebutton(op: HighlightableButton.() -> Unit = {}): HighlightableButton {
    val btn = HighlightableButton()
    btn.op()
    btn.applyColors()
    return btn
}