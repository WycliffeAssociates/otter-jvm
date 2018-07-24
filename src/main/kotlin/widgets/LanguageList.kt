package widgets

import data.Language
import javafx.collections.FXCollections
import javafx.collections.ObservableList

class LanguageList(val languages : List<Language>) {

    val observableList : ObservableList<String> = FXCollections.observableList(mutableListOf())

    init {
        observableList.addAll(languages.map { it.toTextView() })
    }

}


fun Language.toTextView() : String {
    return "${this.slug.toUpperCase()} (${this.name.capitalize()})"
}