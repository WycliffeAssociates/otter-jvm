package persistence.data

import data.Language

class LanguageStore {
    companion object {
        /*val languages: List<Lang> = listOf(
                Lang(1, "en", "English", true, "English"),
                Lang(2, "es", "Espanol", true, "Spanish"),
                Lang(3, "fr", "Français", false, "French"),
                Lang(4, "cmn", "官话", true, "Mandarin Chinese"),
                Lang(5, "ar", "العَرَبِيَّة", false, "Arabic")
        )*/

        val door43Retrieval = Door43Retrieval()
        val langs: List<Lang> = door43Retrieval.getLanguages()
        var languages: MutableList<Language> = MutableList(langs.size){ i ->
            Language(langs[i].pk, langs[i].lc, langs[i].ln, langs[i].gw, langs[i].ang) }

        fun getLanguageForSlug(slug: String) : Language {
            return languages.filter { it.slug == slug }.first()
        }

        fun getById(id: Int) : Language {
            return languages[id - 1]
        }
    }

}