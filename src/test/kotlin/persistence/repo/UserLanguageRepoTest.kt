package persistence.repo

import data.DayNight
import data.Language
import data.User
import data.UserPreferences
import data.dao.Dao
import io.requery.Persistable
import io.requery.reactivex.KotlinReactiveEntityStore
import io.requery.sql.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.sqlite.SQLiteDataSource
import persistence.data.LanguageStore
import persistence.model.Models
import persistence.model.UserEntity
import persistence.model.UserLanguage
import persistence.model.UserPreferencesEntity
import java.sql.SQLException
import kotlin.math.exp

class UserLanguageRepoTest {
    private lateinit var dataStore: KotlinEntityDataStore<Persistable>
    private lateinit var userLanguageRepo: UserLanguageRepo
    private var userId = 0
    private lateinit var inputUserLanguages : MutableList<UserLanguage>

    private val USER_LANGUAGES_TABLE = listOf(
            mapOf(
                    "isSource" to 1,
                    "languageId" to 1
            ),
            mapOf(
                    "isSource" to 0,
                    "languageId" to 1
            ),
            mapOf(
                    "isSource" to 1,
                    "languageId" to 2
            )
    )

    @Before
    fun setup(){
        val dataSource = SQLiteDataSource()
        dataSource.url = "jdbc:sqlite:test.db"

        // creates tables that do not already exist
        SchemaModifier(dataSource, Models.DEFAULT).createTables(TableCreationMode.DROP_CREATE)

        // sets up data store
        val config = KotlinConfiguration(dataSource = dataSource, model = Models.DEFAULT)
        dataStore = KotlinEntityDataStore(config)

        // setup languages
        val languageRepo = LanguageRepo(dataStore)
        LanguageStore.languages.forEach {
            it.id = languageRepo.insert(it).blockingFirst()
        }

        val userPreferenceEntity = UserPreferencesEntity()
        userPreferenceEntity.dayNightMode = DayNight.NIGHT.ordinal
        userPreferenceEntity.preferredTargetLanguageId = 1
        userPreferenceEntity.preferredSourceLanguageId = 2

        val testUserEntity = UserEntity()
        testUserEntity.setUserPreferencesEntity(userPreferenceEntity)
        testUserEntity.setAudioHash("657175390964")
        testUserEntity.setAudioPath("C:\\who\\uses\\windows\\anyway\\satya.wav")

        // insert test user entity
        userId = dataStore.insert(testUserEntity).id

        userLanguageRepo = UserLanguageRepo(dataStore)

        // setup test user languages
        inputUserLanguages = mutableListOf<UserLanguage>()
        for (testCase in USER_LANGUAGES_TABLE) {
            val userLanguageEntity = UserLanguage()
            userLanguageEntity.setSource(testCase["isSource"] == 1)
            userLanguageEntity.setLanguageEntityid(testCase["languageId"] ?: 0)
            userLanguageEntity.setUserEntityid(userId)
            inputUserLanguages.add(userLanguageEntity)
        }
    }

    @Test
    fun testInsertAndRetrieveByUser() {
        inputUserLanguages.forEach {
            userLanguageRepo.insert(it).blockingFirst()
        }

        val result = userLanguageRepo.getByUserId(userId).blockingFirst()

        Assert.assertEquals(inputUserLanguages, result)
    }

    @Test
    fun testInsertThrowsExceptionWithDuplicateEntry() {
        val userLanguageEntity = UserLanguage()
        userLanguageEntity.setSource(true)
        userLanguageEntity.setLanguageEntityid(3)
        userLanguageEntity.setUserEntityid(userId)

        userLanguageRepo.insert(userLanguageEntity).blockingFirst()
        try {
            userLanguageRepo.insert(userLanguageEntity).blockingFirst()
            Assert.fail("Did not fail on second insert")
        } catch (e: StatementExecutionException) {
            // everything passes because exception was thrown
        }
    }

    @Test
    fun testRetrieveAll() {
        inputUserLanguages.forEach {
            userLanguageRepo.insert(it).blockingFirst()
        }
        val result = userLanguageRepo.getAll().blockingFirst()
        Assert.assertEquals(inputUserLanguages, result)
    }

    @Test
    fun testDelete() {
        inputUserLanguages.forEach {
            userLanguageRepo.insert(it).blockingFirst()
        }

        val expected = inputUserLanguages.toMutableList() // use toMutableList to get copy of original list
        expected.remove(inputUserLanguages.first())

        userLanguageRepo.delete(inputUserLanguages.first()).blockingGet()

        val result = userLanguageRepo.getByUserId(userId).blockingFirst()

        Assert.assertEquals(expected, result)
    }

}