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
import recources.UIColors
import tornadofx.*
import java.util.*

/**
 * This class is used to make allow a user to select items from a filterable list
 * and select which of those that are selected as the preferred, or default, item.
 */

class Selector(selectionData : List<DataSelectionInterface>,
                        label : String,
                        hint : String,
                        private val colorAccent : Color,
                        private val updateSelections : PublishSubject<DataSelectionInterface>,
                        private val preferredSelection : PublishSubject<DataSelectionInterface>
) : Fragment() {

    init { messages = ResourceBundle.getBundle("MyView") }

    private val compositeDisposable = CompositeDisposable()
    private val viewModel = SelectorViewModel(updateSelections, preferredSelection)


    private val chips = mutableListOf<Chip>()
    private val selectionList = SelectionList(selectionData)
    private val input = SimpleObjectProperty<String>()


    private val colorNeutral = c(messages["UI_NEUTRAL"])
    private val textFillNeutral = c(messages["UI_NEUTRALTEXT"])

    override val root = vbox {

        alignment = Pos.CENTER

        label(label)

        combobox(input, selectionList.observableList) {

            // Color specific style options are handled in the widget
            // This way a new css rule isn't needed for each color
            style {
                borderColor = multi(box(colorAccent))
                focusColor = colorAccent
            }

            /**
             * Allow filtered searching and filter based on the display string or
             * any other strings affiliated with the data item.
             */

            isEditable = true
            promptText = hint
            makeAutocompletable(false) {
                selectionList.dataList.filter {
                    current -> current.filterText.contains(it)||
                        current.labelText.contains(it, true)
                }.map { it.labelText }.sorted()
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
             * Force the selected value of the combobox to be what is in the text editor.
             * This will force the user to specify which item they want and remove cases
             * where an item is accidentally selected
             */
            editor.textProperty().addListener { _, _, newText -> this.setValue(newText) }

            /**
             * Check and see if a valid item is selected whenever the combobox dropdown is closed.
             * If so, then save the item and make a chip for it.
             */
            addEventFilter(ComboBox.ON_HIDDEN) {
                if (selectionList.observableList.contains(input.value)) {
                    viewModel.addNewValue(selectionList.dataList[selectionList.observableList.indexOf(input.value)])
                }
            }

        }

        separator()

        flowpane {

            /**
             * Whenever new data is obtained on the selected items, redraw the flowpane with the
             * appropriate chips made.
             */
            compositeDisposable.add(updateSelections.subscribe {
                val selection = it
                val check = chips.map { it.labelText == selection.labelText }

                if (check.contains(true)) {
                    chips.removeAt(check.indexOf(true))
                } else {
                    chips.add(0, Chip(
                            selection.labelText,
                            colorAccent,
                            colorNeutral,
                            viewModel::removeSelection,
                            viewModel::newPreferredSelection))
                }

                this.requestFocus()

                children.clear()
                children.addAll((chips.map { it.chip }))
            })

            /**
             * Whenever a chip is clicked to be selected as default, change the color of the
             * chips to represent that action.
             */
            compositeDisposable.add(preferredSelection.subscribe {
                newSelected(it.labelText)
            })

            vgrow = Priority.ALWAYS

            hgap = 6.0
            vgap = 6.0
        }

        padding = Insets(40.0)
        spacing = 10.0

    }

    /**
     * Change the highlighted chip to the one most recently clicked
     */
    private fun newSelected(language : String) {

        for (chip in chips) {
            if (chip.labelText == language) {
                chip.label.textFill = colorNeutral
                chip.button.fill = colorAccent
            } else {
                chip.label.textFill = textFillNeutral
                chip.button.fill = colorNeutral
            }
        }

    }


    /**
     * Dispose of disposables
     */
    override fun onUndock() {
        super.onUndock()
        compositeDisposable.clear()
    }
}
