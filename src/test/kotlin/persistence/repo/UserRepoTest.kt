package persistence.repo

import data.model.Language
import data.model.User
import data.model.UserPreferences
import data.dao.Dao
import io.requery.Persistable
import io.requery.kotlin.eq
import io.requery.reactivex.KotlinReactiveEntityStore
import io.requery.sql.*
import org.junit.*
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
    private lateinit var userLanguageRepo: UserLanguageRepo
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
        userLanguageRepo = UserLanguageRepo(dataStore)
        userRepo = UserRepo(dataStore, userLanguageRepo, languageRepo)
        LanguageStore.languages.forEach {
            it.id = languageRepo.insert(it).blockingFirst()
        }
        val userPreference = UserPreferences(
                id = 0,
                targetLanguage = LanguageStore.languages[2],
                sourceLanguage = LanguageStore.languages[3]

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
            val result = userRepo.getById(it.id).blockingFirst()
            Assert.assertEquals(it, result)
        }
    }

    @Test
    fun insertThrowsExceptionFromDuplicateEntry(){
        users.forEach {
            userRepo.insert(it).blockingFirst()
            try {
                userRepo.insert(it).blockingFirst()
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
        Assert.assertEquals(users, userRepo.getAll().blockingFirst())
    }

    @Test
    fun updateWithAddedLanguagesTest(){
        /*
        users.forEach { user ->
            user.id = userRepo.insert(user).blockingFirst()
            // grab from the db since we need user preferences to have the correct assigned id
            val updatedUser = userRepo.getById(user.id).blockingFirst()
            // get the new source and target slugs from the test case table
            val newSources = USER_DATA_TABLE.filter { it["audioHash"].orEmpty() == user.audioHash }.first()["newSources"].orEmpty().split(",")
            val newTargets = USER_DATA_TABLE.filter { it["audioHash"].orEmpty() == user.audioHash }.first()["newTargets"].orEmpty().split(",")

            // add the new languages from the store
            updatedUser.sourceLanguages.add(LanguageStore.languages.filter { newSources.contains(it.slug) }.first())
            updatedUser.targetLanguages.add(LanguageStore.languages.filter { newTargets.contains(it.slug) }.first())

            // update the repo with this user
            userRepo.update(updatedUser).blockingGet()

            // check the result
            val result = userRepo.getById(updatedUser.id).blockingFirst()
            assertUser(updatedUser, result)
        }
        */
    }

    @Test
    fun updateWithRemovedLanguagesTest(){
        /*
        users.forEach { user ->
            user.id = userRepo.insert(user).blockingFirst()
            // grab from the db since we need user preferences to have the correct assigned id
            val updatedUser = userRepo.getById(user.id).blockingFirst()
            // get the new source and target slugs from the test case table
            val removeSources = USER_DATA_TABLE.filter { it["audioHash"].orEmpty() == user.audioHash }.first()["removeSources"].orEmpty().split(",")
            val removeTargets = USER_DATA_TABLE.filter { it["audioHash"].orEmpty() == user.audioHash }.first()["removeTargets"].orEmpty().split(",")

            // remove some existing languages from the store
            updatedUser.sourceLanguages.remove(LanguageStore.languages.filter { removeSources.contains(it.slug) }.first())
            updatedUser.targetLanguages.remove(LanguageStore.languages.filter { removeTargets.contains(it.slug) }.first())

            // update the repo with this user
            userRepo.update(updatedUser).blockingAwait()

            // check the result
            val result = userRepo.getById(updatedUser.id)
            assertUser(updatedUser, result.blockingFirst())
        }
        */

    }

    @Test
    fun deleteTest() {
        users.forEach { user ->
            user.id = userRepo.insert(user).blockingFirst()
            userRepo.delete(user).blockingAwait()
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
        Assert.assertEquals(expectedUser.userPreferences, actualUser.userPreferences)
    }

    @After
    fun tearDown() {
        dataStore.close()
    }
}