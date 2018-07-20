package widgets

import data.Language
import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
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

    private val languageChips = FXCollections.observableList(mutableListOf<LanguageChip>())

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
                if (languages.contains(input.value) && !selectedLanguages.contains(input.value)) {
                    selectedLanguages.add(0, input.value)
                }
            }

        }

        separator()

        flowpane {

            //children.bind(selectedLanguages, ::addTagNode)

            selectedLanguages.onChange {
                println("change")
                languageChips.clear()
                languageChips.addAll(selectedLanguages.map { addTagNode(it) })

                newSelected(languageChips[0].language) // called in model?

                children.clear()
                children.addAll(languageChips.map { it.chip })
            }

            vgrow = Priority.ALWAYS

            hgap = 6.0
            vgap = 6.0
        }

        padding = Insets(40.0)
        spacing = 10.0

    }

    private fun addTagNode(language : Language) : LanguageChip {

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
                if (textFill == colorNeutral && selectedLanguages.isNotEmpty()) {
                    resetSelected()
                }

                root.requestFocus()
            }

        }

        val background = Rectangle()
        background.fill = colorAccent
        background.arcWidth = 30.0
        background.arcHeight = 30.0
        background.height = 25.0
        // bind the width to the size of the text in the label
        background.widthProperty().bind(label.widthProperty() + deleteButton.widthProperty())

        val chipHbox = HBox(label, deleteButton)

        val chip = LanguageChip(background, label, deleteButton, language, chipStyle, this)
        // dynamic scaling needed

        return chip
    }

    /**
     * If a selected tag is removed, the new selected tag will be
     * the most recently one added
     */
    private fun resetSelected() {
        // get first button
        val firstChip = languageChips[0]

        firstChip.button.fill = colorAccent
        firstChip.label.textFill = colorNeutral

    }

    /**
     * Change the highlighted tag to the one most recently clicked
     */
    fun newSelected(language : Language) {

        for (chip in languageChips) {
            if (chip.language == language) {
                println("here")
                chip.label.textFill = colorNeutral
                chip.button.fill = colorAccent
            } else {
                chip.label.textFill = textFillNeutral
                chip.button.fill = colorNeutral
            }
        }

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

/**
 * This function is used as a toString() for a language object
 */
 fun languageToString(language : Language) : String {
    return "${language.slug} (${language.name})"
}