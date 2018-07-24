package persistence.data

import data.Language

class LanguageStore {
    companion object {
        val languages: List<Language> = listOf(
                Language(0, "en", "English", true, "English"),
                Language(0, "es", "Espanol", true, "Spanish"),
                Language(0, "fr", "Français", false, "French"),
                Language(0, "cmn", "官话", true, "Mandarin Chinese"),
                Language(0, "ar", "العَرَبِيَّة", false, "Arabic")
        )
    }

}