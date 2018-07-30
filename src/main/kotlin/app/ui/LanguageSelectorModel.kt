package app.ui

import data.model.Language
import widgets.ComboBoxSelectionItem

class LanguageSelectorModel(private val languages : List<Language>) {
    var preferredSelection : Language? = null
    val selectedData : MutableList<Language> = mutableListOf()

    fun itemToLanguage(languageItem: ComboBoxSelectionItem) : Language? {
        val language : Language

        try {
            language = languages.first { it.toTextView() == languageItem.labelText }
        } catch(e: NoSuchElementException) {
            return null
        }

        return language
    }
}