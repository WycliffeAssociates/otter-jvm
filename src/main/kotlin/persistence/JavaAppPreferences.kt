package persistence

import data.persistence.AppPreferences
import java.util.prefs.Preferences

// preferences object that stores user-independent preference data
class JavaAppPreferences : AppPreferences {
    private val CURRENT_USER_ID_KEY = "currentUserId"
    private val preferences = Preferences.userNodeForPackage(JavaAppPreferences::class.java)

    override fun getCurrentUserId(): Int {
        return preferences.getInt(CURRENT_USER_ID_KEY, 0)
    }

    override fun setCurrentUserId(userId: Int) {
        preferences.putInt(CURRENT_USER_ID_KEY, userId)
    }
}