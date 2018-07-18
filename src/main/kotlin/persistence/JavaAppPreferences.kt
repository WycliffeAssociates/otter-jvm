package persistence

import data.persistence.AppPreferences
import java.util.prefs.Preferences

// preferences object that stores user-independent preference data
class JavaAppPreferences : AppPreferences {
    private val CURRENT_USER_HASH_KEY = "currentUserHash"
    private val preferences = Preferences.userNodeForPackage(JavaAppPreferences::class.java)

    override fun putCurrentUserHash(userHash: String) {
        preferences.put(CURRENT_USER_HASH_KEY, userHash)
    }

    override fun getCurrentUserHash(): String {
        return preferences.get(CURRENT_USER_HASH_KEY, "")
    }
}