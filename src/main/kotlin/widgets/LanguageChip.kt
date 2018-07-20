package widgets

import data.Language
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.shape.Rectangle
import tornadofx.*

class LanguageChip(val button : Rectangle,
                   val label : Label,
                   val deleteButton : Button,
                   val language : Language,
                   val chipStyle : CssRule,
                   ls : LanguageSelection
) : StackPane() {

    val chip = stackpane {
        add(button)

        add(HBox(label, deleteButton))

        alignment = Pos.CENTER_LEFT
        prefHeight = 25.0
        addClass(chipStyle)
        addEventFilter(MouseEvent.MOUSE_CLICKED) { ls.newSelected(language) }
    }

}