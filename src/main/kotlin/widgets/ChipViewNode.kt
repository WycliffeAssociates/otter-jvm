package widgets

import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.shape.Rectangle

class ChipViewNode(language : String) {

    // creates background for the tag
    val background = Rectangle()

    // dynamic padding?
    val label = Label(language)
    val labelHBox = HBox(label)

    val deleteButton = Button("X")

    val labelDelB = HBox(deleteButton)

    val sp = StackPane(background, labelHBox, labelDelB)
}