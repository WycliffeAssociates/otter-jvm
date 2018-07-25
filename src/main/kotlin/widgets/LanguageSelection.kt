package widgets

import data.Language
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
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
 */

class LanguageSelection(languages : List<Language>,
                        label : String,
                        hint : String,
                        private val colorAccent : Color,
                        private val comboStyle : CssRule,
                        private val chipStyle : CssRule//,
                        //private val preferredLanguage : PublishSubject<Language>
) : Fragment() {

    private val compositeDisposable = CompositeDisposable()
    private val preferredLanguage = PublishSubject.create<Language>()
    private val updateSelectedLanguages = PublishSubject.create<Language>()
    private val viewModel = LanguageSelectorViewModel(updateSelectedLanguages, preferredLanguage)


    private val languageChips = mutableListOf<Chip>()
    private val languageList = LanguageList(languages)
    private val input = SimpleObjectProperty<String>()


    private val colorNeutral = Color.valueOf(UIColors.UI_NEUTRAL)
    private val textFillNeutral = Color.valueOf(UIColors.UI_NEUTRALTEXT)

    override val root = vbox {

        alignment = Pos.CENTER

        label(label)

        combobox(input, languageList.observableList) {

            addClass(comboStyle)

            /**
             * Allow filtered searching and filter based on a language's name,
             * slug, and anglicized name
             */

            isEditable = true
            promptText = hint
            makeAutocompletable(false) {
                languageList.languages.filter {
                    current -> current.slug.contains(it, true) ||
                        current.anglicizedName.contains(it, true) ||
                        current.name.contains(it, true) ||
                        current.toTextView().contains(it, true)
                }.map { it.toTextView() }.sorted() // .sorted() could be replaced by a better sorter
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

            editor.textProperty().addListener { _, _, newText -> this.setValue(newText) }

            /**
             * Make a selectable chip button when the dropdown closes and a valid language
             * is selected
             */
            addEventFilter(ComboBox.ON_HIDDEN) {
                if (languageList.observableList.contains(input.value)) {
                    viewModel.addNewLanguage(languageList.languages[languageList.observableList.indexOf(input.value)])
                }
            }

        }

        separator()

        flowpane {

            // Redraw the flowpane if the number of chips change
            compositeDisposable.add(updateSelectedLanguages.subscribe {
                val language = it
                val check = languageChips.map { it.labelText == language.toTextView() }

                if (check.contains(true)) {
                    languageChips.removeAt(check.indexOf(true))
                } else {
                    languageChips.add(0, Chip(
                            language.toTextView(),
                            chipStyle,
                            colorNeutral,
                            textFillNeutral,
                            viewModel::removeLanguage,
                            viewModel::newPreferredLanguage))
                }

                children.clear()
                children.addAll((languageChips.map { it.chip }))
            })

            // Select the new preferred language
            compositeDisposable.add(preferredLanguage.subscribe {
                newSelected(it.toTextView())
            })

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
    private fun newSelected(language : String) {

        for (chip in languageChips) {
            if (chip.labelText == language) {
                chip.label.textFill = colorNeutral
                chip.button.fill = colorAccent
            } else {
                chip.label.textFill = textFillNeutral
                chip.button.fill = colorNeutral
            }
        }

    }


    override fun onUndock() {
        super.onUndock()
        compositeDisposable.clear()
    }
}


