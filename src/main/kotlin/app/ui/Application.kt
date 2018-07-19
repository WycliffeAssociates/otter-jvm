package app.ui

import data.DayNight
import data.Language
import data.User
import data.UserPreferences
import persistence.injection.DaggerDatabaseComponent
import java.util.*

object Application {
    @JvmStatic
    fun main(args: Array<String>) {
        println("hello")
        val database = DaggerDatabaseComponent.builder().build().inject()

        val language = Language(
                slug = "fr",
                name = "François",
                anglicizedName = "French",
                isGateway = true
        )

        try {
            database.getLanguageDao().insert(language).subscribe()
        } catch (e: Exception) {
            println("error inserting language: already exists, use update instead")
        }

        val retrievedLanguage = database.getLanguageDao().getAll().blockingFirst().filter { it.slug == "fr" }.first()

        var user: User
        try {
            user = database.getUserDao().getById(1).blockingFirst()
            println("getting an existing user")
            var newLanguage = Language(
                    slug = UUID.randomUUID().toString(),
                    name = "",
                    anglicizedName = "",
                    isGateway = true
            )
            newLanguage.id = database.getLanguageDao().insert(newLanguage).blockingFirst()
            user.sourceLanguages.clear()
            user.targetLanguages.clear()
            user.sourceLanguages.add(newLanguage)
            user.targetLanguages.add(newLanguage)
            user.userPreferences.preferredSourceLanguage = newLanguage
            user.userPreferences.preferredTargetLanguage = newLanguage
        } catch (e: Exception) {
            println("inserting a new user")
            val userPreferences = UserPreferences(
                    preferredSourceLanguage = retrievedLanguage,
                    preferredTargetLanguage = retrievedLanguage,
                    dayNightMode = DayNight.DAY,
                    uiLanguagePreferences = "fr"
            )

            user = User(
                    audioHash = "12345678",
                    audioPath = "my/audio/path/file.wav",
                    sourceLanguages = mutableListOf(retrievedLanguage),
                    targetLanguages = mutableListOf(retrievedLanguage),
                    userPreferences = userPreferences
            )

            user.id = database.getUserDao().insert(user).blockingFirst()
            user = database.getUserDao().getById(user.id).blockingFirst()
            println("new user: ${user}")
        }
        println("changing the preferences")
        user.userPreferences.dayNightMode = DayNight.DAY
        user.userPreferences.uiLanguagePreferences = UUID.randomUUID().toString()
        println("new preferences: ${user.userPreferences}")

        try {
            database.getUserDao().insert(user).subscribe()
        } catch (e: Exception) {
            println("error inserting user: already exists, updating instead")
            database.getUserDao().update(user).subscribe()
        }

        val retrievedUser = database.getUserDao().getAll().blockingFirst().first()

        println("retrieved user: ${retrievedUser}")

        if (retrievedUser.userPreferences != user.userPreferences) {
            println("FAILED TO UPDATE USER PREFERENCES")
        } else {
            println("User preference update OK")
        }
    }
}