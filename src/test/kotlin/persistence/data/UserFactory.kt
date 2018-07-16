package persistence.data

import data.User
import java.util.*
import kotlin.collections.ArrayList

object UserFactory {
    fun randomUuid(): String{
        return UUID.randomUUID().toString()
    }

    fun makeUser(): User {
        return User(
                audioHash = randomUuid(),
                audioPath = randomUuid(),
                sourceLanguages = LanguageFactory.makeLanguages(10).toMutableList(),
                targetLanguages = LanguageFactory.makeLanguages(10).toMutableList(),
                preferredSourceLanguage = LanguageFactory.makeLanguage(),
                preferredTargetLanguage = LanguageFactory.makeLanguage()
                )
    }

    fun makeUserList(count: Int): List<User> {
        val tmp = ArrayList<User>()
        repeat(count){
            tmp.add(makeUser())
        }
        return tmp
    }
}
