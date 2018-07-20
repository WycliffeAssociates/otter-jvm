package persistence.data

import data.Language

class LanguageStore {
    companion object {
        /*val languages: List<Language> = listOf(
                Language(1, "en", "English", true, "English"),
                Language(2, "es", "Espanol", true, "Spanish"),
                Language(3, "fr", "Français", false, "French"),
                Language(4, "cmn", "官话", true, "Mandarin Chinese"),
                Language(5, "ar", "العَرَبِيَّة", false, "Arabic")
        )*/
        val door43Retrieval = Door43Retrieval()
        val languages: List<Language> = door43Retrieval.getLanguages()
        fun hi(){
            for (lang in languages){
                println(lang.name)
            }
        }

        fun getLanguageForSlug(slug: String) : Language {
            return languages.filter { it.slug == slug }.first()
        }

        fun getById(id: Int) : Language {
            return languages[id - 1]
        }
    }

}