package org.wycliffeassociates.otter.jvm.app.widgets.otterchrome

import de.jensd.fx.glyphs.icons525.Icons525
import de.jensd.fx.glyphs.icons525.Icons525View
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIcon
import de.jensd.fx.glyphs.materialdesignicons.MaterialDesignIconView
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import org.wycliffeassociates.otter.jvm.app.widgets.highlightablebutton.highlightablebutton
import tornadofx.*

class OtterChrome : HBox() {

    init {

        minHeight = 64.0
        maxHeight = 64.0
        prefHeight = 64.0

        hgrow = Priority.ALWAYS
        vgrow = Priority.NEVER

        Insets(0.0, 10.0, 0.0, 0.0)

        background = Background(BackgroundFill(Paint.valueOf("#E0E0E0"), null, null))

        alignment = Pos.CENTER_RIGHT

        add(
        highlightablebutton {
            text = "Home"
            graphic = MaterialDesignIconView(MaterialDesignIcon.HOME)
            secondaryColor = Color.valueOf("#E0E0E0")
            isHighlightedProperty.set(false)
            style {
                fontSize = Dimension(16.0, Dimension.LinearUnits.pt)
            }
        }
        )
        add(
        highlightablebutton {
            text = "Profile"
            graphic = Icons525View(Icons525.USER)
            secondaryColor = Color.valueOf("#E0E0E0")
            isHighlightedProperty.set(false)
            style {
                fontSize = Dimension(16.0, Dimension.LinearUnits.pt)
            }
        }
        )
        add(
        highlightablebutton {
            text = "Settings"
            graphic = MaterialDesignIconView(MaterialDesignIcon.SETTINGS)
            secondaryColor = Color.valueOf("#E0E0E0")
            isHighlightedProperty.set(false)
            style {
                fontSize = Dimension(16.0, Dimension.LinearUnits.pt)
            }
        }
        )
    }
}