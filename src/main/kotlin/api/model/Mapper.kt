package api.model
import api.Lang
import data.Language
import persistence.model.LanguageEntity

class Mapper {
    fun map(lang: Lang): Language{
        return Language(lang.pk, lang.lc, lang.ln, lang.gw, lang.ang)
    }
}