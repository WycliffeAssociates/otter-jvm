package persistence.mapping

import data.Language
import data.User
import data.dao.Dao
import io.requery.Persistable
import io.requery.sql.KotlinConfiguration
import io.requery.sql.KotlinEntityDataStore
import io.requery.sql.SchemaModifier
import io.requery.sql.TableCreationMode
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.sqlite.SQLiteDataSource
import persistence.data.LanguageStore
import persistence.model.Models
import persistence.model.UserEntity
import persistence.model.UserLanguage
import persistence.repo.LanguageRepo
import persistence.repo.UserRepo
import java.util.*

class UserMapperTest {
    private lateinit var dataStore: KotlinEntityDataStore<Persistable>
    private lateinit var languageRepo: Dao<Language>
    private lateinit var users: MutableList<User>

    val USER_DATA_TABLE = listOf(
            mapOf(
                    "audioHash" to "12345678",
                    "audioPath" to "/my/really/long/path/name.wav",
                    "targetSlugs" to "ar",
                    "sourceSlugs" to "en,cmn"
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
        LanguageStore.languages.forEach {
            it.id = languageRepo.insert(it).blockingFirst()
        }
        users = ArrayList()
        USER_DATA_TABLE.forEach {testCase ->
            users.add(
                    User(
                            audioHash = testCase["audioHash"].orEmpty(),
                            audioPath = testCase["audioPath"].orEmpty(),
                            targetLanguages = LanguageStore.languages.filter { testCase["targetSlugs"].orEmpty().split(",").contains(it.slug) }.toMutableList(),
                            sourceLanguages = LanguageStore.languages.filter { testCase["sourceSlugs"].orEmpty().split(",").contains(it.slug) }.toMutableList()
                    )
            )
        }
    }

    @Test
    fun testIfUserEntityCorrectlyMappedToUser() {
        for (testCase in USER_DATA_TABLE) {
            val input = UserEntity()
            input.id = Random().nextInt()
            input.setAudioHash(testCase["audioHash"])
            input.setAudioPath(testCase["audioPath"])

            val expected = User(
                    id = input.id,
                    audioHash = input.audioHash,
                    audioPath = input.audioPath,
                    targetLanguages = LanguageStore.languages.filter { testCase["targetSlugs"].orEmpty().split(",").contains(it.slug) }.toMutableList(),
                    sourceLanguages = LanguageStore.languages.filter { testCase["sourceSlugs"].orEmpty().split(",").contains(it.slug) }.toMutableList()
            )

            expected.id = dataStore.insert(input).id
            val userLanguageRef = UserLanguage()
            userLanguageRef.setUserEntityid(expected.id)
            expected.sourceLanguages.union(expected.targetLanguages).forEach{
                userLanguageRef.setLanguageEntityid(it.id)
                userLanguageRef.setSource(expected.sourceLanguages.contains(it))
                dataStore.insert(userLanguageRef)
            }

            val result = UserMapper(dataStore).mapFromEntity(input)
            try {
                Assert.assertEquals(expected, result)
            } catch (e: AssertionError) {
                println("Input: ${input.audioHash}")
                println("Result: ${result.audioHash}")
                throw e
            }
        }
    }

    @Test
    fun testIfLanguageCorrectlyMappedToLanguageEntity() {
        for (testCase in USER_DATA_TABLE) {
            val expected = UserEntity()
            expected.id = Random().nextInt()
            expected.setAudioHash(testCase["audioHash"])
            expected.setAudioPath(testCase["audioPath"])

            val input = User(
                    id = expected.id,
                    audioHash = expected.audioHash,
                    audioPath = expected.audioPath,
                    targetLanguages = LanguageStore.languages.filter { testCase["targetSlugs"].orEmpty().split(",").contains(it.slug) }.toMutableList(),
                    sourceLanguages = LanguageStore.languages.filter { testCase["sourceSlugs"].orEmpty().split(",").contains(it.slug) }.toMutableList()
            )

            val result = UserMapper(dataStore).mapToEntity(input)
            try {
                Assert.assertEquals(expected, result)
            } catch (e: AssertionError) {
                println("Input: ${expected.audioHash}")
                println("Result: ${result.audioHash}")
                throw e
            }
        }
    }

}