package widgets

import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.effect.DropShadow
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
           fillColor : Color,
           textColor : Color,
           onDelete : (Chip) -> Unit,
           onClick : (Chip) -> Unit
) : StackPane() {

    val label : Label
    val deleteButton : Button
    val button : Rectangle
    val chip : StackPane

    init {

        label = label(labelText) {
            textFill = textColor

        }

        deleteButton = button("X") {
            textFillProperty().bind(label.textFillProperty())

            action {
                onDelete(this@Chip)
            }

        }

        button = rectangle {
            fill = fillColor
            height = 25.0

            // bind the width to the size of the text in the label
            widthProperty().bind(label.widthProperty() + deleteButton.widthProperty())
        }

        chip = stackpane {

            setOnMouseEntered {
                effect = DropShadow(5.0, fillColor)
            }
            setOnMouseExited {
                effect = null
            }

            add(button)

            add(HBox(label, deleteButton))

            //addClass(chipStyle)
            addEventFilter(MouseEvent.MOUSE_CLICKED) { onClick(this@Chip) }


        }
        //button.heightProperty().bind(chip.heightProperty())

    }

}
