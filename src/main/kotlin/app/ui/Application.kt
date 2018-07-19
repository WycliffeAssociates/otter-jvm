package app.ui

import data.DayNight
import data.Language
import data.User
import data.UserPreferences
import persistence.injection.DaggerDatabaseComponent

object Application {
    @JvmStatic
    fun main(args: Array<String>) {
        println("hello")
        val database = DaggerDatabaseComponent.builder().build().inject()
        val language = Language(
                slug = "en",
                name = "English",
                anglicizedName = "English",
                canBeSource = true
        )

        try {
            database.getLanguageDao().insert(language)
        } catch (e: Exception) {
            println("error inserting language: already exists, use update instead")
        }

        val retrievedLanguage = database.getLanguageDao().getAll().blockingFirst().first()

        val userPreferences = UserPreferences(
                preferredSourceLanguage = retrievedLanguage,
                preferredTargetLanguage = retrievedLanguage,
                dayNightMode = DayNight.NIGHT
        )

        val user = User(
                audioHash = "12345678",
                audioPath = "my/audio/path/file.wav",
                sourceLanguages = mutableListOf(retrievedLanguage),
                targetLanguages = mutableListOf(retrievedLanguage),
                userPreferences = userPreferences
        )

        try {
            database.getUserDao().update(user)
        } catch (e: Exception) {
            println("error inserting user: already exists, try update instead")
        }

        val retrievedUsers = database.getUserDao().getAll().blockingFirst()

        println(retrievedUsers)
    }
}