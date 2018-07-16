package persistence.data

import data.Language
import java.util.*
import kotlin.collections.ArrayList

object LanguageFactory {
    fun randomUuid(): String{
        return UUID.randomUUID().toString()
    }

    fun makeLanguage(): Language{
        return Language(
                name = randomUuid(),
                slug = randomUuid(),
                canBeSource = true,
                anglicizedName = randomUuid()
        )
    }

    fun makeLanguages(count: Int): List<Language>{
        val languages = ArrayList<Language>()
        repeat(count){
            languages.add(makeLanguage())
        }
        return languages
    }
}