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
 * @param onDelete Function that details what should be done when the delete button is clicked.
 * @param onClick Function that details what should be done when the chip is clicked.
 */
class Chip(
        val slug: String,
        val name: String,
        onDelete : (Chip) -> Unit,
        onClick : (Chip) -> Unit
) : StackPane() {

    val slugLabel : Label
    val nameLabel : Label
    val deleteButton : Button
    val button : Rectangle

    init {

        slugLabel = label(slug) {
            setId("slugLabel")
        }
        nameLabel = label("(" + name + ")") { setId("nameLabel") }
        nameLabel.textFillProperty().bind(slugLabel.textFillProperty())

        deleteButton = button {
            val deleteIcon = MaterialIconView(MaterialIcon.CLEAR, "20px")
            deleteIcon.fillProperty().bind(slugLabel.textFillProperty())
            add(deleteIcon)
            action {
                onDelete(this@Chip)
            }
        }

        button = rectangle {
            height = 25.0

            // bind the width to the size of the text in the label
            widthProperty().bind(slugLabel.widthProperty() + nameLabel.widthProperty()
                    + deleteButton.widthProperty())
        }

        add(button)
        add(HBox(slugLabel, nameLabel, deleteButton))

        addEventFilter(MouseEvent.MOUSE_CLICKED) { onClick(this) }
    }

}
