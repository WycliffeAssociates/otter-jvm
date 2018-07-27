package widgets

import javafx.collections.FXCollections
import javafx.collections.ObservableList

class ComboBoxSelectionList(val dataList : List<ComboBoxSelectionItem>) {
    val observableList : ObservableList<String> = FXCollections.observableList(mutableListOf())

    init {
        observableList.addAll(dataList.map { it.labelText })
    }
}