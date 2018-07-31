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
 * A chip is a StackPane object containing a label, a delete button, and a rectangle (for the background);
 * it can be clicked or deleted, and functions are passed in to define what is done on clicking or deleting.
 *
 * @author Caleb Benedick and Kimberly Horton
 *
 * @param labelText The text to be displayed on the chip.
 * @param dropShadowColor Color of the dropShadow that appears on hover. (I think this is moved out in widget3.)
 * @param onDelete Function that details what should be done when the delete button is clicked.
 * @param onClick Function that details what should be done when the chip is clicked.
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
