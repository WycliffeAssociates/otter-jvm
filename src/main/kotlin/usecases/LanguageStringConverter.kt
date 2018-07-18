package usecases

import data.Language
import javafx.util.StringConverter

class LanguageStringConverter : StringConverter<Language>() {
    val mapLanguage = mutableMapOf<String, Language>()

    override fun fromString(string: String?): Language? = string?.let { mapLanguage[it] }

    override fun toString(language : Language?): String? {
        val output = "${language?.slug} (${language?.name})"
        if (language != null && !mapLanguage.containsKey(output)) mapLanguage[output] = language
        return output
    }
}