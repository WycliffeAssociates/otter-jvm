package widgets

import data.Language
import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.util.StringConverter
import recources.UIColors
import tornadofx.*

/**
 * This class is used to make the drop-downs for adding new
 * target or source languages and adds highlightable and
 * deletable buttons for each language selected
 *
 *
 *   -------- PLEASE NOTE ---------
 * This will eventually need a reference to the Profile variable
 * observables once it is made ready in the common component
 *
 * KNOWN BUGS:
 * Deleting / using enter / clicking will keep / place the language related
 * language in the ComboBox text field
 *
 * A button is made whenever the comboBox is closed with any text in it
 *
 * IDEAS:
 * Find a way to pass the style sheet in and have on hover, selected, and neutral classes for each one
 */

class LanguageSelection(languages : ObservableList<Language>,
                        input : SimpleObjectProperty<Language>,
                        label : String,
                        hint : String,
                        private val colorAccent : Color,
                        private val comboStyle : CssRule,
                        private val chipStyle : CssRule,
                        private val selectedLanguages : ObservableList<Language>
) : Fragment() {

    private val colorNeutral = Color.valueOf(UIColors.UI_NEUTRAL)
    private val textFillNeutral = Color.valueOf(UIColors.UI_NEUTRALTEXT)

    private val fp = flowpane {

        children.bind(selectedLanguages, ::addTagNode)

        selectedLanguages.onChange {
            println(selectedLanguages.toString())
        }
        vgrow = Priority.ALWAYS

        hgap = 6.0
        vgap = 6.0
    }

    override val root = vbox {

        alignment = Pos.CENTER

        label(label)

        combobox(input, languages) {

            addClass(comboStyle)

            // set what is displayed in list with custom converter
            converter = LanguageStringConverter()

            /**
             * Allow filtered searching and filter based on a language's name,
             * slug, and anglicized name
             */
            isEditable = true
            promptText = hint
            makeAutocompletable(false) {
                languages.observable().filtered {
                    current -> current.slug.contains(it, true) ||
                        current.name.contains(it, true) ||
                        current.anglicizedName.contains(it, true)

                }.sorted()
            }

            /**
             * Remove any text in the textfield when it is refocused
             */
            editor.focusedProperty().addListener {
                obs, old, new -> run {
                    Platform.runLater {
                        if (editor.isFocused && !editor.text.isEmpty()) {
                            editor.selectAll()
                        }
                    }
                }
            }

            /**
             * make a selectable chip button when the dropdown closes and a valid language
             * is selected
             */
            addEventFilter(ComboBox.ON_HIDDEN) {
                println(input)
                println(this.editor.text)
                //if (/*languages.map { languageToString(it) }.contains(this.editor.text)*/) {
                    if (languages.contains(input.value) && !selectedLanguages.contains(input.value)) {
                        selectedLanguages.add(0, input.value)
                    }
                //}
            }

        }

        separator()

        add(fp) // should probably find a better solution, but reference to flowpane is needed outside of root

        padding = Insets(40.0)
        spacing = 10.0

    }

    // Given by the awesome Carl
    private fun addTagNode(language : Language) : StackPane {

        val background = Rectangle()
        background.fill = colorAccent
        background.arcWidth = 30.0
        background.arcHeight = 30.0
        background.height = 25.0

        // dynamic padding?
        val label = label(languageToString(language)) {
            textFill = colorNeutral
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
                selectedLanguages.remove( userData as Language )
                if (background.fill == colorAccent && selectedLanguages.isNotEmpty()) {
                    resetSelected()
                }

                root.requestFocus()
            }

        }

        val chipHbox = HBox(label, deleteButton)

        // bind the width to the size of the text in the label
        background.widthProperty().bind(label.widthProperty() + deleteButton.widthProperty())

        val chip = StackPane(background, chipHbox)
        // dynamic scaling needed
        chip.alignment = Pos.CENTER_LEFT
        chip.prefHeight = 25.0
        chip.addClass(chipStyle)
        chip.addEventFilter(MouseEvent.MOUSE_CLICKED) { newSelected(language) }

        if (fp.children.isNotEmpty()) {
            newSelected(language)
        }

        return chip
    }

    /**
     * If a selected tag is removed, the new selected tag will be
     * the most recently one added
     */
    private fun resetSelected() {
        // get first button
        val firstTag = fp.children[0]

        for (nodeOut in firstTag.getChildList().orEmpty()) {
            if (nodeOut is Rectangle) {
                nodeOut.fill = colorAccent
            } else if (nodeOut is HBox) {
                for (nodeIn in nodeOut.children) {
                    if (nodeIn is Label) {
                        nodeIn.textFill = colorNeutral
                    }
                }
            }
        }

    }

    /**
     * Change the highlighted tag to the one most recently clicked
     */
    private fun newSelected(tag : Language) {

        val elements = fp.children
        var rectangleReference = Rectangle()

        // for all objects in the flowpane
        for (children in elements) {

            // for all children in each flowpane object
            for (nodeOut in children.getChildList().orEmpty()) {
                // change the rectangle color
                // and keep a reference
                if (nodeOut is Rectangle) {
                    rectangleReference = nodeOut
                    nodeOut.fill = colorNeutral
                } else if (nodeOut is HBox) {

                    // find the label and check if it equals the selected label
                    for (nodeIn in nodeOut.children) {

                        // if so, then highlight the rectangle color
                        if (nodeIn is Label) {
                            nodeIn.textFill = textFillNeutral
                            if (nodeIn.text == languageToString(tag)) {
                                nodeIn.textFill = colorNeutral
                                rectangleReference.fill = colorAccent
                            }
                            /*} else if (nodeIn is Button) {
                            //nodeIn.textFill = textFillNeutral
                            if (/*rectangleReference.fill == colorAccent*/ true) {
                                //nodeIn.textFill = colorNeutral
                            }
                        }*/
                        }
                    }
                }
            }
        }

    }

    /**
     * This function is used as a toString() for a language object
     */
    private fun languageToString(language : Language) : String {
        return "${language.slug} (${language.name})"
    }

}


/**
 * This converter is used for the comboBox in order to display language objects as strings
 * in the dropdown
 */
class LanguageStringConverter : StringConverter<Language>() {
    private val mapLanguage = mutableMapOf<String, Language>()

    override fun fromString(string: String?): Language? = string?.let { mapLanguage[it] }

    override fun toString(language : Language?): String? {
        val output = "${language?.slug} (${language?.name})"
        if (language != null && !mapLanguage.containsKey(output)) mapLanguage[output] = language
        return output
    }

}
