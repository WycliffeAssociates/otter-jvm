package widgets

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import tornadofx.*

/**
 * A language chip is a class used to store the data of the chip for
 * easy direct access. This will allow access to the stackpane, and elements
 * in the stackpane without searching through the children on the parent of the
 * stackpane for what we want to access.
 */

class Chip(val labelText : String,
           chipStyle : CssRule,
           fillColor : Color,
           textColor : Color,
           onDelete : (Chip) -> Unit,
           onClick : (Chip) -> Unit
) : StackPane() {

    val label : Label = label(labelText) {
        textFill = textColor
        alignment = Pos.CENTER_LEFT
        padding = Insets(10.0, 0.0, 10.0, 20.0)
    }

    val deleteButton : Button = button("X") {
        //userData = language // pro tip (thanks Carl)
        textFillProperty().bind(label.textFillProperty())
        opacity = 0.65
        alignment = Pos.CENTER_RIGHT
        padding = Insets(12.0, 10.0, 5.0, 10.0)

        action {
            onDelete(this@Chip)
        }

    }

    val button : Rectangle = rectangle {
        fill = fillColor
        arcWidth = 30.0
        arcHeight = 30.0
        height = 25.0

        // bind the width to the size of the text in the label
        // typecast recursion error?
        widthProperty().bind(label.widthProperty() + deleteButton.widthProperty())
    }

    val chip : StackPane = stackpane {
        add(button)

        add(HBox(label, deleteButton))

        alignment = Pos.CENTER_LEFT
        prefHeight = 25.0
        addClass(chipStyle)
        addEventFilter(MouseEvent.MOUSE_CLICKED) { onClick(this@Chip) } //setPreferredChip
    }

}
