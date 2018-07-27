package app.ui

import data.model.Language
import widgets.ComboBoxSelectionItem

class LanguageSelectionItem(language: Language) : ComboBoxSelectionItem {
    override val labelText = language.toTextView()
    override val filterText = listOf(language.name, language.slug, language.anglicizedName)
}

fun Language.toTextView() : String {
    return "${this.slug.toUpperCase()} (${this.name.capitalize()})"
}