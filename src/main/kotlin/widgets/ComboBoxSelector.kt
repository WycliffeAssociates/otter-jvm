package widgets

import javafx.application.Platform
import javafx.scene.control.ComboBox
import tornadofx.*

class ComboBoxSelector (
        selectionData: List<ComboBoxSelectionItem>,
        hint: String,
        onComboBoxHidden : (ComboBoxSelectionItem) -> Unit
) : ComboBox<String>() {

    init {
        val comboBoxSelectionList = ComboBoxSelectionList(selectionData)

        items = comboBoxSelectionList.observableList
        /** Set up filterable combobox based on the incoming data to select from */
        isEditable = true
        promptText = hint
        makeAutocompletable(false) {
            comboBoxSelectionList.dataList.filter { current ->
                current.filterText.contains(it) ||
                        current.labelText.contains(it, true)
            }.map { it.labelText }.sorted()
        }

        /** Select any text in the textfield when it is refocused */
        editor.focusedProperty().addListener { _, _, _ ->
            run {
                Platform.runLater {
                    if (editor.isFocused && !editor.text.isEmpty()) {
                        editor.selectAll()
                    }
                }
            }
        }

        /** Set the combobox selected value to the value in the text editor */
        editor.textProperty().addListener { _, _, newText -> value = newText }

        /** Add valid any valid item when the combobox closes */
        addEventFilter(ComboBox.ON_HIDDEN) {
            if (comboBoxSelectionList.observableList.contains(value)) {
                val index = comboBoxSelectionList.observableList.indexOf(value)
                onComboBoxHidden(comboBoxSelectionList.dataList[index])
            }
        }
    }

}

