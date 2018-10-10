package org.wycliffeassociates.otter.jvm.app.widgets.filterablecombobox

import javafx.collections.FXCollections
import javafx.scene.control.ComboBox
import javafx.scene.layout.Pane
import tornadofx.*

/**
 * This class contains a comboBox that is searchable and filterable through a text field.
 * It auto selects any text in the field when refocusing back on the comboBox
 *
 * @author Caleb Benedick
 * @author Matthew Russell
 */
class FilterableComboBox<T> : ComboBox<T>() {
    val filterItems = FXCollections.observableArrayList<FilterableItem<T>>()
    init {
        /** Set up filterable comboBox based on the incoming data to select from */
        isEditable = true
        makeAutocompletable(false) { input ->
            items.filter { item ->
                filterItems
                        .filter { it.item == item }
                        .firstOrNull()
                        ?.filterText
                        ?.joinToString("&")
                        ?.contains(input, true) ?: false
            }
        }

        /** Select any text in the editor when it is refocused */
        editor.focusedProperty().onChange {
            if (editor.isFocused && !editor.text.isEmpty()) {
                editor.selectAll()
            }
        }
    }
}

fun <T> Pane.filterablecombobox(init: FilterableComboBox<T>.() -> Unit = {}): FilterableComboBox<T> {
    val box = FilterableComboBox<T>()
    box.init()
    add(box)
    return box
}
