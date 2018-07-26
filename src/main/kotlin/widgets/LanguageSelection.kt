package widgets

import data.Language

class LanguageSelection(language : Language) : DataSelectionInterface {
    override val labelText = language.toTextView()
    override val filterText = listOf(language.name, language.slug, language.anglicizedName)
}


fun Language.toTextView() : String {
    return "${this.slug.toUpperCase()} (${this.name.capitalize()})"
}