package widgets

import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import recources.UIColors
import tornadofx.*


/**
 * This class is used to make the drop-downs for adding new
 * target or source languages and adds highlightable and
 * deletable buttons for each language selected.
 *
 *
 *   -------- PLEASE NOTE ---------
 * This will eventually need a reference to the Profile variable
 * observables once it is made ready in the common component.
 *
 * KNOWN BUGS:
 * >>  Deleting / using enter / clicking will keep / place the language related
 *     language in the ComboBox text field
 * >>  Current implementation requires manual input of colors and not just a stylesheet
 *     (not really a bug, but needs to be fixed)
 * >>  Caleb and Kimberly worked on this, so it's just a given that there are more bugs
 *     and even poorer function- and variable-naming practices
 *
 *   --------- PARAMETERS ---------
 * @list - observable list of strings (languages)
 * @input - the user's selected string
 * @label - title to go above the ComboBox
 * @hint - string for hint text inside the combobox
 * @colorAccent - the accent color for the box and chips
 * @comboStyle - CssRule styling for combobox
 * @chipViewStyle - CssRule styling for the chips appearing under the combobox
 * @selectedLanguages - an observable list of strings to contain the user's selected options
 */
class LanguageSelection(list : ObservableList<String>,
                        input : SimpleStringProperty,
                        label : String,
                        hint : String,
                        private val colorAccent : Color,
                        private val comboStyle : CssRule,
                        private val chipViewStyle : CssRule,
                        private val selectedLanguages : ObservableList<String>
) : Fragment() {

    // pull colors and styles needed into private variables
        /* my (kimberly's) prof will be really disappointed
        ** that I didn't put underscores in front of the private var names */
    private val colorNeutral = Color.valueOf(UIColors.UI_NEUTRAL)
    private val colorNeutralText = Color.valueOf(UIColors.UI_NEUTRALTEXT)

    private val fp = flowpane {

        children.bind(selectedLanguages, ::addTagNode)

        vgrow = Priority.ALWAYS

        hgap = 6.0
        vgap = 6.0
    }

    override val root = vbox {

        alignment = Pos.CENTER

        label(label)

        combobox(input, list) {

            /**
             * Give it some personal space
             */
            vboxConstraints {
                marginLeft = 10.0
                marginTop = 10.0
                marginRight = 10.0
                marginBottom = 10.0
            }

            // add styling for the combobox
            addClass(comboStyle)

            /**
             * Allow filtered searching
             */
            isEditable = true
            makeAutocompletable(false)

            promptText = hint

            // TODO: Find out how to shorten lambda syntax
            addEventFilter(ComboBox.ON_HIDDEN, {
                if( input.isNotEmpty.value && list.contains(input.value) && !selectedLanguages.contains(input.value) ) {
                    selectedLanguages.add(0, input.value )
                }
            })
        }

        separator {}

        add(fp) // should find better solution, but reference to flowplane is needed outside of root

        padding = Insets(40.0)
        spacing = 10.0

    }

    /**
     * Adds the buttons / toggles to the screen
     */
    private fun addTagNode(language : String) : Node {

        // creates background for the tag
        val background = Rectangle()
        background.fill = colorAccent
            // will need to make the dimensions dynamic (maybe)
        background.arcWidth = 30.0
        background.arcHeight = 30.0
        background.height = 25.0

        // dynamic padding?
        val label = label(language) {
            padding = Insets(10.0, 0.0, 10.0, 20.0)
            alignment = Pos.CENTER_LEFT
            textFill = colorNeutral
        }

        // TODO: only create this if the language has no projects in it!
        val deleteButton = button("X") {
            userData = language // pro tip (thanks Carl)
            textFill = colorNeutral
            opacity = 0.65
            alignment = Pos.CENTER_RIGHT
            padding = Insets(12.0, 10.0, 5.0, 10.0)

            action {
                selectedLanguages.remove(userData as String)
                if (background.fill == colorAccent && selectedLanguages.isNotEmpty()) {
                    resetSelected()
                }
            }
        }

        // place the label and button into an HBox
        val buttonsHBox = HBox(label, deleteButton)

        // bind the background's width to the label and button width
        background.widthProperty().bind(label.widthProperty() + deleteButton.widthProperty())

        val sp = StackPane(background, buttonsHBox)
        sp.alignment = Pos.CENTER_LEFT // THIS MAKES IT WORK, DON'T TOUCH IT (╯°□°）╯︵ ┻━┻
        sp.prefHeight = 25.0

        // add hover effect class
        sp.addClass(chipViewStyle)

        if (fp.children.isNotEmpty()) {
            newSelected(language)
        }

        // Find out how to shorten lambda syntax
        sp.addEventFilter(MouseEvent.MOUSE_CLICKED, EventHandler<MouseEvent> { mouseEvent -> newSelected(language)})
        return sp
    }

    /**
     * If a selected tag is removed, the new selected tag will be
     * the most recent one added
     */
    private fun resetSelected() {
        // get first button in the list
        val firstTag = fp.children[0]
        for (nodeOut in firstTag.getChildList().orEmpty()) {
            // make the rectangle the accent color
            if (nodeOut is Rectangle) {
                nodeOut.fill = colorAccent
            } else if (nodeOut is HBox) {
                for (nodeIn in nodeOut.children) {
                    // set label and button text to white
                    if (nodeIn is Label) {
                        nodeIn.textFill = colorNeutral
                    } else if (nodeIn is Button) {
                        nodeIn.textFill = colorNeutral
                    }
                }
            }
        }
    }

    /**
     * Change the highlighted tag to the one most recently clicked
     */
    private fun newSelected(tag : String) {

        val elements = fp.children
        var rectangleReference = Rectangle()

        // for all objects in the flowpane
        for (children in elements) {
            // for all children in each flowplane object
            for (nodeOut in children.getChildList().orEmpty()) {
                // change the rectangle color
                // and kek* a reference
                    // *originally keep but the typo was too good
                if (nodeOut is Rectangle) {
                    rectangleReference = nodeOut
                    nodeOut.fill = colorNeutral
                } else if (nodeOut is HBox) {
                    // find the label and check if it equals the selected label
                    for (nodeIn in nodeOut.children) {
                        // if so, then highlight the rectangle color and change the text color
                        if (nodeIn is Label) {
                            nodeIn.textFill = colorNeutralText
                            if (nodeIn.text == tag) {
                                rectangleReference.fill = colorAccent
                                nodeIn.textFill = colorNeutral
                            }
                            // and also change the button color according to the rectangle fill
                        } else if (nodeIn is Button) {
                            nodeIn.textFill = colorNeutral
                            if (rectangleReference.fill == colorNeutral) {
                                nodeIn.textFill = colorNeutralText
                            }
                        }
                    }
                }
            }
        }

    }

}