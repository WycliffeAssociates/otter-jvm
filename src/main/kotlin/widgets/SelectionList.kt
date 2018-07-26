package widgets

import javafx.collections.FXCollections
import javafx.collections.ObservableList

class SelectionList(val dataList : List<DataSelectionInterface>) {

    val observableList : ObservableList<String> = FXCollections.observableList(mutableListOf())

    init {
        observableList.addAll(dataList.map { it.labelText })
    }

}