package persistence.repo

import data.model.Language
import data.model.User
import data.model.UserPreferences
import data.dao.Dao
import io.requery.Persistable
import io.requery.cache.EntityCacheBuilder
import io.requery.cache.WeakEntityCache
import io.requery.kotlin.eq
import io.requery.sql.*
import org.junit.*
import org.sqlite.SQLiteDataSource
import persistence.data.LanguageStore
import persistence.model.IUserLanguage
import persistence.model.Models

class UserRepoTest {
    private lateinit var dataStore: KotlinEntityDataStore<Persistable>
    private lateinit var userRepo: UserRepo
    private lateinit var languageRepo: Dao<Language>
    private lateinit var userLanguageRepo: UserLanguageRepo
    private lateinit var users: MutableList<User>

    val USER_DATA_TABLE = listOf(
            mapOf(
                    "audioHash" to "12345678",
                    "audioPath" to "/my/really/long/path/name.wav",
                    "targetSlugs" to "ar,gln",
                    "sourceSlugs" to "en,cmn",
                    "newTargets" to "es",
                    "newSources" to "fr",
                    "removeTargets" to "ar",
                    "removeSources" to "en",
                    "newPrefSource" to "cmn",
                    "newPrefTarget" to "gln"
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
                targetLanguage = LanguageStore.getLanguageForSlug("ar"),
                sourceLanguage = LanguageStore.getLanguageForSlug("en")
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
    fun addLanguagesTest(){
        users.forEach { user ->
            user.id = userRepo.insert(user).blockingFirst()
            // grab from the db since we need user preferences to have the correct assigned id
            val updatedUser = userRepo.getById(user.id).blockingFirst()

            // get the new source and target slugs from the test case table
            val newSourceSlugs = USER_DATA_TABLE.filter { it["audioHash"].orEmpty() == user.audioHash }
                    .first()["newSources"].orEmpty().split(",")
            val newTargetSlugs = USER_DATA_TABLE.filter { it["audioHash"].orEmpty() == user.audioHash }
                    .first()["newTargets"].orEmpty().split(",")

            // add the new languages from the store
            val newSources = LanguageStore.languages.filter { newSourceSlugs.contains(it.slug) }
            val newTargets = LanguageStore.languages.filter { newTargetSlugs.contains(it.slug) }

            newSources.forEach {
                userRepo.addLanguage(user, it, true).blockingAwait()
            }

            newTargets.forEach {
                userRepo.addLanguage(user, it, false).blockingAwait()
            }

            // check the result
            val result = userRepo.getById(updatedUser.id).blockingFirst()
            Assert.assertTrue(result.sourceLanguages.containsAll(newSources))
            Assert.assertTrue(result.targetLanguages.containsAll(newTargets))
        }

    }

    @Test
    fun removedLanguagesTest(){
        users.forEach { user ->
            user.id = userRepo.insert(user).blockingFirst()
            // grab from the db since we need user preferences to have the correct assigned id
            val updatedUser = userRepo.getById(user.id).blockingFirst()

            // get the new source and target slugs from the test case table
            val removeSourcesSlugs = USER_DATA_TABLE.filter { it["audioHash"].orEmpty() == user.audioHash }
                    .first()["removeSources"].orEmpty().split(",")
            val removeTargetsSlugs = USER_DATA_TABLE.filter { it["audioHash"].orEmpty() == user.audioHash }
                    .first()["removeTargets"].orEmpty().split(",")

            val removeSources = LanguageStore.languages.filter { removeSourcesSlugs.contains(it.slug) }
            val removeTargets = LanguageStore.languages.filter { removeTargetsSlugs.contains(it.slug) }

            // remove some existing languages from the store
            removeSources.forEach {
                userRepo.removeLanguage(user, it, true).blockingAwait()
            }
            removeTargets.forEach {
                userRepo.removeLanguage(user, it, false).blockingAwait()
            }

            // check the result
            val result = userRepo.getById(updatedUser.id).blockingFirst()
            Assert.assertTrue(result.sourceLanguages.all { !removeSources.contains(it) })
            Assert.assertTrue(result.targetLanguages.all { !removeTargets.contains(it) })
        }

    }

    @Test
    fun setSourceLanguageTest() {
        users.forEach { user ->
            user.id = userRepo.insert(user).blockingFirst()
            user.userPreferences.id = user.id
            val newSourceSlug = USER_DATA_TABLE.filter { it["audioHash"] == user.audioHash }
                    .first()["newPrefSource"] ?: ""
            val newSource = LanguageStore.getLanguageForSlug(newSourceSlug)
            userRepo.setLanguagePreference(user, newSource, true).blockingAwait()

            val result = userRepo.getById(user.id).blockingFirst()
            Assert.assertEquals(newSource, result.userPreferences.sourceLanguage)
        }
    }

    @Test
    fun setTargetLanguageTest() {
        users.forEach { user ->
            user.id = userRepo.insert(user).blockingFirst()
            user.userPreferences.id = user.id
            val newTargetSlug = USER_DATA_TABLE.filter { it["audioHash"] == user.audioHash }
                    .first()["newPrefTarget"] ?: ""
            val newTarget = LanguageStore.getLanguageForSlug(newTargetSlug)
            userRepo.setLanguagePreference(user, newTarget, false).blockingAwait()

            val result = userRepo.getById(user.id).blockingFirst()
            Assert.assertEquals(newTarget, result.userPreferences.targetLanguage)
        }
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

    @After
    fun tearDown() {
        dataStore.close()
    }
}