package app.ui

import data.model.Language
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.effect.DropShadow
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import tornadofx.*
import widgets.*
import java.util.ResourceBundle

/**
 * This class is used to make allow a user to select items from a filterable list
 * and select which of those that are selected as the preferred, or default, item.
 */

class LanguageSelector(
        languages: List<Language>,
        label: String,
        hint: String,
        colorResourcesFile: String,
        private val colorAccent: Color,
        private val updateLanguages: PublishSubject<Language>,
        private val preferredLanguage: PublishSubject<Language>
) : Fragment() {

    private val selectionData : List<ComboBoxSelectionItem>
    private val compositeDisposable : CompositeDisposable
    private val chips : MutableList<Chip>
    private val viewModel : LanguageSelectorViewModel

    override val root = VBox()


    init {
        messages = ResourceBundle.getBundle(colorResourcesFile)

        compositeDisposable = CompositeDisposable()
        selectionData = languages.map { LanguageSelectionItem(it) }
        chips = mutableListOf()
        viewModel = LanguageSelectorViewModel(updateLanguages, preferredLanguage, languages)

        with(root) {

            alignment = Pos.CENTER

            label(label)
            this += ComboBoxSelector(selectionData, hint, viewModel::addNewValue).apply {
                style {
                    focusColor = colorAccent
                    borderColor = multi(box(colorAccent))
                }
            }
            separator()

            flowpane {

                /** Redraw the flowpane with any new data */
                compositeDisposable.add(
                        updateLanguages.subscribe {
                            val language = it
                            val check = chips.map { it.labelText == language.toTextView() }

                            if (check.contains(true)) {
                                chips.removeAt(check.indexOf(true))
                            } else {
                                chips.add(0,
                                        Chip(
                                                language.toTextView(),
                                                viewModel::removeLanguage,
                                                viewModel::newPreferredLanguage
                                        ).apply {
                                            setOnMouseEntered {
                                                effect = DropShadow(5.0, colorAccent)
                                            }
                                            setOnMouseExited {
                                                effect = null
                                            }
                                        }
                                )
                            }

                            this.requestFocus()
                            children.clear()
                            children.addAll(chips)
                        }
                )

                /** Change the chip colors based on which one is selected */
                compositeDisposable.add(
                        preferredLanguage.subscribe {
                            newSelected(it.toTextView())
                        }
                )

                vgrow = Priority.ALWAYS
                hgap = 6.0
                vgap = 6.0
            }

            padding = Insets(40.0)
            spacing = 10.0
        }
    }

    /** Change the highlighted chip to the one most recently clicked */
    private fun newSelected(language: String) {
        chips.first().requestFocus()
        for (chip in chips) {
            if (chip.labelText == language) {
                chip.label.textFill = c(messages["UI_NEUTRAL"])
                chip.button.fill = colorAccent
            } else {
                chip.label.textFill = c(messages["UI_NEUTRAL_TEXT"])
                chip.button.fill = c(messages["UI_NEUTRAL"])
            }
        }
    }

    /** Dispose of disposables */
    override fun onUndock() {
        super.onUndock()
        compositeDisposable.clear()
    }
}