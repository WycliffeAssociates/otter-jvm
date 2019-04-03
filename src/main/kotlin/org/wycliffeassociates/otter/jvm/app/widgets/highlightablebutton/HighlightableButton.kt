package org.wycliffeassociates.otter.jvm.app.widgets.highlightablebutton

import com.jfoenix.controls.JFXButton
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.paint.Color
import tornadofx.*

class HighlightableButton(
        private val primaryColor: Color,
        private val secondaryColor: Color,
        private val hasBorder: Boolean,
        private val isHighlighted: SimpleBooleanProperty,
        private val icon: MaterialIconView? = null
) : JFXButton() {

    init {
        importStylesheet<HighlightableButtonStyles>()

        addClass(HighlightableButtonStyles.hButton)
        isDisableVisualFocus = true
        graphic = icon

        applyColors()
        isHighlighted.onChange {
            applyColors()
        }
        hoverProperty().onChange {
            applyColors()
        }
    }

    private fun applyColors() {
        if (hoverProperty().get() || isHighlighted.get()) {
            applyColors(primaryColor, secondaryColor)
        } else {
            applyColors(secondaryColor, primaryColor)
        }
    }

    private fun applyColors(bgColor: Color, contentFillColor: Color) {
        style {
            backgroundColor += bgColor
            if(hasBorder) {
                borderColor += box(primaryColor)
            }
        }
        icon?.apply {
            fill = contentFillColor
        }
        textFill = contentFillColor
    }
}