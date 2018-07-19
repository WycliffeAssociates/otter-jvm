package persistence.mapping

import data.DayNight
import data.User
import data.UserPreferences
import io.reactivex.Observable
import io.requery.Persistable
import io.requery.kotlin.eq
import io.requery.query.Result

import io.requery.sql.KotlinEntityDataStore
import org.junit.Assert
import org.junit.Test
import org.mockito.Answers
import org.mockito.BDDMockito
import org.mockito.Mock
import org.mockito.Mockito
import persistence.data.LanguageStore
import persistence.model.*
import persistence.repo.LanguageRepo
import java.util.*
import kotlin.math.exp

class UserMapperTest {
    private val mockDataStore = Mockito.mock(KotlinEntityDataStore::class.java, Answers.RETURNS_DEEP_STUBS) as KotlinEntityDataStore<Persistable>
    private val mockLanguageDao = Mockito.mock(LanguageRepo::class.java)

    val USER_DATA_TABLE = listOf(
            mapOf(
                    "id" to "42",
                    "audioHash" to "12345678",
                    "audioPath" to "/my/really/long/path/name.wav",
                    "targetSlugs" to "ar",
                    "sourceSlugs" to "en,cmn"
            )
    )
    /*
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
    */
    @Test
    fun testIfUserEntityCorrectlyMappedToUser() {
        for (testCase in USER_DATA_TABLE) {
            val input = UserEntity()
            input.id = testCase["id"].orEmpty().toInt()
            input.setAudioHash(testCase["audioHash"])
            input.setAudioPath(testCase["audioPath"])
            val inputUserPreferencesEntity = UserPreferencesEntity()
            inputUserPreferencesEntity.uiLanguagePreference = "en"
            inputUserPreferencesEntity.dayNightMode = DayNight.NIGHT.ordinal
            inputUserPreferencesEntity.preferredTargetLanguageId = 2
            inputUserPreferencesEntity.preferredSourceLanguageId = 3
            input.setUserPreferencesEntity(inputUserPreferencesEntity)

            val expectedUserPreferences = UserPreferences(
                    id = 0,
                    dayNightMode = DayNight.NIGHT,
                    preferredTargetLanguage = LanguageStore.languages[2],
                    preferredSourceLanguage = LanguageStore.languages[3]
            )
            val expected = User(
                    id = input.id,
                    audioHash = input.audioHash,
                    audioPath = input.audioPath,
                    targetLanguages = LanguageStore.languages.filter { testCase["targetSlugs"].orEmpty().split(",").contains(it.slug) }.toMutableList(),
                    sourceLanguages = LanguageStore.languages.filter { testCase["sourceSlugs"].orEmpty().split(",").contains(it.slug) }.toMutableList(),
                    userPreferences = expectedUserPreferences
            )


            val sourceUserLanguageEntities = expected.sourceLanguages.map {
                val mockUserLanguageEntity = Mockito.mock(UserLanguage::class.java)
                BDDMockito.given(mockUserLanguageEntity.languageEntityid).willReturn(it.id)
                BDDMockito.given(mockUserLanguageEntity.source).willReturn(true)
                mockUserLanguageEntity
            }

            val targetUserLanguageEntities = expected.targetLanguages.map {
                val mockUserLanguageEntity = Mockito.mock(UserLanguage::class.java)
                BDDMockito.given(mockUserLanguageEntity.languageEntityid).willReturn(it.id)
                BDDMockito.given(mockUserLanguageEntity.source).willReturn(false)
                mockUserLanguageEntity
            }

            println(sourceUserLanguageEntities)

            val mockSourceResult = Mockito.mock(Result::class.java) as Result<IUserLanguage>
            BDDMockito.given(mockDataStore.select(IUserLanguage::class).where((IUserLanguage::userEntityid eq input.id) and (IUserLanguage::source eq true)).get())
                    .willReturn(mockSourceResult)
            BDDMockito.given(mockSourceResult.toList())
                    .willReturn(sourceUserLanguageEntities)

            val mockTargetResult = Mockito.mock(Result::class.java) as Result<IUserLanguage>
            BDDMockito.given(mockDataStore.select(IUserLanguage::class).where((IUserLanguage::userEntityid eq input.id) and (IUserLanguage::source eq false)).get())
                    .willReturn(mockTargetResult)
            BDDMockito.given(mockTargetResult.toList())
                    .willReturn(targetUserLanguageEntities)

            BDDMockito.given(mockLanguageDao.getById(Mockito.anyInt())).will {
                Observable.just(LanguageStore.getById(it.getArgument(0)))
            }

            val result = UserMapper(mockDataStore, mockLanguageDao).mapFromEntity(input)
            try {
                Assert.assertEquals(expected, result)
            } catch (e: AssertionError) {
                println("Input: ${input.audioHash}")
                println("Result: ${result.audioHash}")
                throw e
            }
        }
    }
/*
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
                    sourceLanguages = LanguageStore.languages.filter { testCase["sourceSlugs"].orEmpty().split(",").contains(it.slug) }.toMutableList(),

            )

            val result = UserMapper(mockDataStore, mockLanguageDao).mapToEntity(input)
            try {
                Assert.assertEquals(expected, result)
            } catch (e: AssertionError) {
                println("Input: ${expected.audioHash}")
                println("Result: ${result.audioHash}")
                throw e
            }
        }
    }

*/
}