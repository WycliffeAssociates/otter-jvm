package widgets

import data.Language

class LanguageSelectorModel {

    var preferredLanguage : Language? = null

    val selectedLanguages : MutableList<Language> = mutableListOf()

}