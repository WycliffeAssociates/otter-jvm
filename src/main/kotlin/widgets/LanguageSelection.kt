package widgets

import data.Language
import io.reactivex.subjects.PublishSubject
import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.util.StringConverter
import recources.UIColors
import tornadofx.*

/**
 * This class is used to make the drop-downs for adding new
 * target or source languages and adds highlightable and
 * deletable buttons where the 'selected' button is the preferred
 * language.
 *
 * KNOWN BUGS:
 * Deleting / using enter / clicking will keep / place the language related
 * language in the ComboBox text field
 *
 * A button is made whenever the comboBox is closed with any text in it
 */

class LanguageSelection(languages : ObservableList<Language>,
                        input : SimpleObjectProperty<Language>,
                        label : String,
                        hint : String,
                        private val colorAccent : Color,
                        private val comboStyle : CssRule,
                        private val chipStyle : CssRule,
                        private val selectedLanguages : PublishSubject<List<Language>>,
                        private val preferredLanguage : PublishSubject<Language>
) : Fragment() {

    private val viewModel = LanguageSelectorViewModel(selectedLanguages, preferredLanguage)

    private val colorNeutral = Color.valueOf(UIColors.UI_NEUTRAL)
    private val textFillNeutral = Color.valueOf(UIColors.UI_NEUTRALTEXT)

    private val languageChips = mutableListOf<LanguageChip>()

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
             * Select any text in the textfield when it is refocused
             */
            editor.focusedProperty().addListener {
                _, _, _ -> run {
                    Platform.runLater {
                        if (editor.isFocused && !editor.text.isEmpty()) {
                            editor.selectAll()
                        }
                    }
                }
            }

            /**
             * Make a selectable chip button when the dropdown closes and a valid language
             * is selected
             */
            addEventFilter(ComboBox.ON_HIDDEN) {
                if (languages.contains(input.value)) {
                    viewModel.addNewLanguage(input.value)
                }
            }

        }

        separator()

        flowpane {

            // Redraw the flowpane if the number of chips change
            selectedLanguages.subscribe {
                languageChips.clear()
                languageChips.addAll(it.map { LanguageChip(it, chipStyle, colorNeutral, textFillNeutral, viewModel) })

                children.clear()
                children.addAll((languageChips.map { it.chip }))
            }

            // Select the new preferred language
            preferredLanguage.subscribe {
                newSelected(it)
            }

            vgrow = Priority.ALWAYS

            hgap = 6.0
            vgap = 6.0
        }

        padding = Insets(40.0)
        spacing = 10.0

    }

    /**
     * Change the highlighted tag to the one most recently clicked
     */
    fun newSelected(language : Language) {

        for (chip in languageChips) {
            if (chip.language == language) {
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
