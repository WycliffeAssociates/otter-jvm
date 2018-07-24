package api.model
import api.Lang
import data.Language
import persistence.model.LanguageEntity

class Mapper {
    //maps the local lang data class to the common language data class
    fun mapToCommon(lang: Lang): Language{
        return Language(lang.pk, lang.lc, lang.ln, lang.gw, lang.ang)
    }
}