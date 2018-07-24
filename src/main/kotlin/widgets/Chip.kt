package widgets

import data.Language
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import org.intellij.lang.annotations.Language
import tornadofx.*

/**
 * A language chip is a class used to store the data of the chip for
 * easy direct access. This will allow access to the stackpane, and elements
 * in the stackpane without searching through the children on the parent of the
 * stackpane for what we want to access.
 */

class LanguageChip(val language: Language,
                   chipStyle : CssRule,
                   fillColor : Color,
                   textColor : Color,
                   viewModel : LanguageSelectorViewModel
) : StackPane() {

    val label = label(languageToString(language)) {
        textFill = textColor
        alignment = Pos.CENTER_LEFT
        padding = Insets(10.0, 0.0, 10.0, 20.0)
    }

    val deleteButton = button("X") {
        userData = language // pro tip (thanks Carl)
        textFillProperty().bind(label.textFillProperty())
        opacity = 0.65
        alignment = Pos.CENTER_RIGHT
        padding = Insets(12.0, 10.0, 5.0, 10.0)

        action {
            viewModel.removeLanguage(language)
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

    val chip = stackpane {
        add(button)

        add(HBox(label, deleteButton))

        alignment = Pos.CENTER_LEFT
        prefHeight = 25.0
        addClass(chipStyle)
        addEventFilter(MouseEvent.MOUSE_CLICKED) { viewModel.setPreferedLanguage(language) }
    }

}
