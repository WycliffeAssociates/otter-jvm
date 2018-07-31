package widgets

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.ComboBox
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.*

/**
 * This class is used to make allow a user to select items from a filterable list
 * and select which of those that are selected as the preferred, or default, item.
 */

class ComboBoxSelector(
        selectionData: List<ComboBoxSelectionItem>,
        label: String,
        hint: String,
        private val colorAccent: Color,
        private val colorNeutral: Color,
        private val colorTextNeutral: Color,
        private val updateSelections: PublishSubject<ComboBoxSelectionItem>,
        private val preferredSelection: PublishSubject<ComboBoxSelectionItem>
) : Fragment() {

    private val compositeDisposable = CompositeDisposable()
    private val viewModel = ComboBoxSelectorViewModel(updateSelections, preferredSelection)

    private val chips = mutableListOf<Chip>()
    private val comboBoxSelectionList = ComboBoxSelectionList(selectionData)
    private val input = SimpleObjectProperty<String>()

    override val root = vbox {

        alignment = Pos.CENTER

        label(label)

        combobox(input, comboBoxSelectionList.observableList) {

            // Color specific style options are handled in the widget
            // This way a new css rule isn't needed for each color
            style {
                borderColor = multi(box(colorAccent))
                focusColor = colorAccent
            }

            /** Set up filterable combobox based on the incoming data to select from */
            isEditable = true
            promptText = hint
            makeAutocompletable(false) {
                comboBoxSelectionList.dataList.filter {
                    current -> current.filterText.contains(it)||
                        current.labelText.contains(it, true)
                }.map { it.labelText }.sorted()
            }

            /** Select any text in the textfield when it is refocused */
            editor.focusedProperty().addListener {
                _, _, _ -> run {
                    Platform.runLater {
                        if (editor.isFocused && !editor.text.isEmpty()) {
                            editor.selectAll()
                        }
                    }
                }
            }

            /** Set the combobox selected value to the value in the text editor */
            editor.textProperty().addListener { _, _, newText -> this.setValue(newText) }

            /** Add valid any valid item when the combobox closes */
            addEventFilter(ComboBox.ON_HIDDEN) {
                if (comboBoxSelectionList.observableList.contains(input.value)) {
                    val index = comboBoxSelectionList.observableList.indexOf(input.value)
                    viewModel.addNewValue(comboBoxSelectionList.dataList[index])
                }
            }

        }

        separator()

        flowpane {

            /** Redraw the flowpane with any new data */
            compositeDisposable.add(
                    updateSelections.subscribe {
                        val selection = it
                        val check = chips.map { it.labelText == selection.labelText }

                        if (check.contains(true)) {
                            chips.removeAt(check.indexOf(true))
                        } else {
                            chips.add(0,
                                    Chip(
                                        selection.labelText,
                                            colorAccent,
                                            colorNeutral,
                                        viewModel::removeSelection,
                                        viewModel::newPreferredSelection
                                    )
                            )
                        }

                        this.requestFocus()
                        children.clear()
                        children.addAll((chips.map { it.chip }))
                    }
            )

            /** Change the chip colors based on which one is selected */
            compositeDisposable.add(
                    preferredSelection.subscribe {
                        newSelected(it.labelText)
                    }
            )

            vgrow = Priority.ALWAYS
            hgap = 6.0
            vgap = 6.0
        }

        padding = Insets(40.0)
        spacing = 10.0
    }

    /** Change the highlighted chip to the one most recently clicked */
    private fun newSelected(language : String) {
        for (chip in chips) {
            if (chip.labelText == language) {
                chip.label.textFill = colorNeutral
                chip.button.fill = colorAccent
            } else {
                chip.label.textFill = colorTextNeutral
                chip.button.fill = colorNeutral
            }
        }
    }

    /** Dispose of disposables */
    override fun onUndock() {
        super.onUndock()
        compositeDisposable.clear()
    }
}