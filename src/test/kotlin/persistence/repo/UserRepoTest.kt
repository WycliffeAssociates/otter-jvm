package persistence.repo

import data.DayNight
import data.Language
import data.User
import data.UserPreferences
import data.dao.Dao
import io.requery.Persistable
import io.requery.kotlin.eq
import io.requery.sql.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.sqlite.SQLiteDataSource
import persistence.data.LanguageStore
import persistence.mapping.LanguageMapper
import persistence.mapping.UserMapper
import persistence.mapping.UserPreferencesMapper
import persistence.model.IUserLanguage
import persistence.model.Models
import persistence.model.UserPreferencesEntity

class UserRepoTest {
    private lateinit var dataStore: KotlinEntityDataStore<Persistable>
    private lateinit var userRepo: Dao<User>
    private lateinit var languageRepo: Dao<Language>
    private lateinit var users: MutableList<User>

    val USER_DATA_TABLE = listOf(
            mapOf(
                    "audioHash" to "12345678",
                    "audioPath" to "/my/really/long/path/name.wav",
                    "targetSlugs" to "ar",
                    "sourceSlugs" to "en,cmn",
                    "newTargets" to "es",
                    "newSources" to "fr",
                    "removeTargets" to "ar",
                    "removeSources" to "en"
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

        languageRepo = LanguageRepo(dataStore)
        userRepo = UserRepo(dataStore, languageRepo)
        LanguageStore.languages.forEach {
            it.id = languageRepo.insert(it).blockingFirst()
        }
        val userPreference = UserPreferences(
                id = 0,
                dayNightMode = DayNight.NIGHT,
                preferredTargetLanguage = LanguageStore.languages[2],
                preferredSourceLanguage = LanguageStore.languages[3]
        )
        users = ArrayList()
        USER_DATA_TABLE.forEach {testCase ->
            users.add(
                    User(
                            audioHash = testCase["audioHash"].orEmpty(),
                            audioPath = testCase["audioPath"].orEmpty(),
                            targetLanguages = LanguageStore.languages.filter { testCase["targetSlugs"].orEmpty().split(",").contains(it.slug) }.toMutableList(),
                            sourceLanguages = LanguageStore.languages.filter { testCase["sourceSlugs"].orEmpty().split(",").contains(it.slug) }.toMutableList(),
                            userPreferences = userPreference
                    )
            )
        }
    }

    @Test
    fun insertAndRetrieveTest(){
        users.forEach {
            it.id = userRepo.insert(it).blockingFirst()
            it.userPreferences.id = it.id
            val result = userRepo.getById(it.id)
            result.test().assertValue(it)
        }
    }

    @Test
    fun insertThrowsExceptionFromDuplicateEntry(){
        users.forEach {
            userRepo.insert(it)
            try {
                userRepo.insert(it)
                Assert.fail()
            } catch (e: StatementExecutionException) {
                // success
            }
        }
    }

    @Test
    fun retrieveAllTest(){
        users.forEach {
            it.id = userRepo.insert(it).blockingFirst()
            it.userPreferences.id = it.id
        }
        userRepo.getAll().test().assertValue(users)
    }

    @Test
    fun updateWithAddedLanguagesTest(){
        users.forEach { user ->
            user.id = userRepo.insert(user).blockingFirst()
            // get the new source and target slugs from the test case table
            val newSources = USER_DATA_TABLE.filter { it["audioHash"].orEmpty() == user.audioHash }.first()["newSources"].orEmpty().split(",")
            val newTargets = USER_DATA_TABLE.filter { it["audioHash"].orEmpty() == user.audioHash }.first()["newTargets"].orEmpty().split(",")

            // add the new languages from the store
            user.sourceLanguages.add(LanguageStore.languages.filter { newSources.contains(it.slug) }.first())
            user.targetLanguages.add(LanguageStore.languages.filter { newTargets.contains(it.slug) }.first())

            // update the repo with this user
            userRepo.update(user).test().assertComplete()

            // check the result
            val result = userRepo.getById(user.id)
            assertUser(user, result.blockingFirst())
        }
    }

    @Test
    fun updateWithRemovedLanguagesTest(){
        users.forEach { user ->
            user.id = userRepo.insert(user).blockingFirst()
            // get the new source and target slugs from the test case table
            val removeSources = USER_DATA_TABLE.filter { it["audioHash"].orEmpty() == user.audioHash }.first()["removeSources"].orEmpty().split(",")
            val removeTargets = USER_DATA_TABLE.filter { it["audioHash"].orEmpty() == user.audioHash }.first()["removeTargets"].orEmpty().split(",")

            // remove some existing languages from the store
            user.sourceLanguages.remove(LanguageStore.languages.filter { removeSources.contains(it.slug) }.first())
            user.targetLanguages.remove(LanguageStore.languages.filter { removeTargets.contains(it.slug) }.first())

            // update the repo with this user
            userRepo.update(user).test().assertComplete()

            // check the result
            val result = userRepo.getById(user.id)
            assertUser(user, result.blockingFirst())
        }

    }

    @Test
    fun deleteTest() {
        users.forEach { user ->
            userRepo.delete(user).test().assertComplete()
            val result = dataStore.select(IUserLanguage::class).where(IUserLanguage::userEntityid eq user.id).get()
            Assert.assertTrue(result.toList().isEmpty())
        }

    }

    fun assertUser(expectedUser: User, actualUser: User){
        Assert.assertEquals(expectedUser.id, actualUser.id)
        Assert.assertEquals(expectedUser.audioHash, actualUser.audioHash)
        Assert.assertEquals(expectedUser.audioPath, actualUser.audioPath)
        expectedUser.targetLanguages.forEach{
            Assert.assertTrue(actualUser.targetLanguages.contains(it))
        }
        expectedUser.sourceLanguages.forEach{
            Assert.assertTrue(actualUser.sourceLanguages.contains(it))
        }
    }
}