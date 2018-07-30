package app.ui

import data.model.Language
import widgets.ComboBoxSelectionItem

class LanguageSelectorModel(private val languages : List<Language>) {
    var preferredSelection : Language? = null
    val selectedData : MutableList<Language> = mutableListOf()

    fun itemToLanguage(languageItem: ComboBoxSelectionItem) : Language {
        return languages.first { it.toTextView() == languageItem.labelText }
    }
}