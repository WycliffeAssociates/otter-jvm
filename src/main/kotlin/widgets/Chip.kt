package widgets

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
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
 *
 * @param labelText The text to be displayed on the chip.
 * @param fillColor the color
 */

class Chip(
        val labelText: String,
        onDelete : (Chip) -> Unit,
        onClick : (Chip) -> Unit
) : StackPane() {

    val label : Label
    val deleteButton : Button
    val button : Rectangle

    init {

        label = label(labelText)

        deleteButton = button {
            val deleteIcon = MaterialIconView(MaterialIcon.CLEAR, "20px")
            deleteIcon.fillProperty().bind(label.textFillProperty())
            add(deleteIcon)
            action {
                onDelete(this@Chip)
            }
        }

        button = rectangle {
            height = 25.0

            // bind the width to the size of the text in the label
            widthProperty().bind(label.widthProperty() + deleteButton.widthProperty())
        }

        add(button)
        add(HBox(label, deleteButton))

        addEventFilter(MouseEvent.MOUSE_CLICKED) { onClick(this) }
    }

}
