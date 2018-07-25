package persistence.mapping

import data.model.User
import data.model.UserPreferences
import io.reactivex.Observable

import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito
import org.mockito.Mockito
import persistence.data.LanguageStore
import persistence.model.*
import persistence.repo.LanguageRepo
import persistence.repo.UserLanguageRepo

class UserDoor43MapperTest {
    private val mockUserLanguageRepo = Mockito.mock(UserLanguageRepo::class.java)
    private val mockLanguageDao = Mockito.mock(LanguageRepo::class.java)

    val USER_DATA_TABLE = listOf(
            mapOf(
                    "id" to "42",
                    "audioHash" to "12345678",
                    "audioPath" to "/my/really/long/path/name.wav",
                    "targetSlugs" to "ar",
                    "sourceSlugs" to "en,cmn",
                    "preferredSource" to "cmn",
                    "preferredTarget" to "ar"
            ),
            mapOf(
                    "id" to "10",
                    "audioHash" to "abcdef",
                    "audioPath" to "/my/path/name.wav",
                    "targetSlugs" to "es,fr",
                    "sourceSlugs" to "en,es",
                    "preferredSource" to "es",
                    "preferredTarget" to "es"
            )
    )

    @Before
    fun setup() {
        BDDMockito.given(mockLanguageDao.getById(Mockito.anyInt())).will {
            Observable.just(LanguageStore.getById(it.getArgument(0)))
        }
    }

    @Test
    fun testIfUserEntityCorrectlyMappedToUser() {
        for (testCase in USER_DATA_TABLE) {
            // setup input
            val input = UserEntity()
            input.id = testCase["id"].orEmpty().toInt()
            input.setAudioHash(testCase["audioHash"])
            input.setAudioPath(testCase["audioPath"])
            val inputUserPreferencesEntity = UserPreferencesEntity()
            inputUserPreferencesEntity.id = input.id
            inputUserPreferencesEntity.setTargetLanguageId(LanguageStore.languages.filter { testCase["preferredTarget"] == it.slug }.first().id)
            inputUserPreferencesEntity.setSourceLanguageId(LanguageStore.languages.filter { testCase["preferredSource"] == it.slug }.first().id)
            input.setUserPreferencesEntity(inputUserPreferencesEntity)

            // setup matching expected
            val expectedUserPreferences = UserPreferences(
                    id = input.id,
                    targetLanguage = LanguageStore.languages.filter { testCase["preferredTarget"] == it.slug }.first(),
                    sourceLanguage = LanguageStore.languages.filter { testCase["preferredSource"] == it.slug }.first()
            )
            val expected = User(
                    id = input.id,
                    audioHash = input.audioHash,
                    audioPath = input.audioPath,
                    targetLanguages = LanguageStore.languages.filter { testCase["targetSlugs"].orEmpty().split(",").contains(it.slug) }.toMutableList(),
                    sourceLanguages = LanguageStore.languages.filter { testCase["sourceSlugs"].orEmpty().split(",").contains(it.slug) }.toMutableList(),
                    userPreferences = expectedUserPreferences
            )

            val allUserLanguageEntities = expected.sourceLanguages.map {
                val userLanguageEntity = UserLanguage()
                userLanguageEntity.setUserEntityid(expected.id)
                userLanguageEntity.setLanguageEntityid(it.id)
                userLanguageEntity.setSource(true)
                userLanguageEntity
            }.union(expected.targetLanguages.map {
                val userLanguageEntity = UserLanguage()
                userLanguageEntity.setUserEntityid(expected.id)
                userLanguageEntity.setLanguageEntityid(it.id)
                userLanguageEntity.setSource(false)
                userLanguageEntity
            }).toList()

            BDDMockito.given(mockUserLanguageRepo.getByUserId(input.id)).will {
                Observable.just(allUserLanguageEntities)
            }

            val result = UserMapper(mockUserLanguageRepo, mockLanguageDao).mapFromEntity(input)
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
    fun testIfUserCorrectlyMappedToUserEntity() {
        for (testCase in USER_DATA_TABLE) {
            val input = User(
                    id = testCase["id"].orEmpty().toInt(),
                    audioHash = testCase["audioHash"].orEmpty(),
                    audioPath = testCase["audioPath"].orEmpty(),
                    targetLanguages = LanguageStore.languages.filter { testCase["targetSlugs"].orEmpty().split(",").contains(it.slug) }.toMutableList(),
                    sourceLanguages = LanguageStore.languages.filter { testCase["sourceSlugs"].orEmpty().split(",").contains(it.slug) }.toMutableList(),
                    userPreferences = UserPreferences(
                            id = testCase["id"].orEmpty().toInt(),
                            targetLanguage = LanguageStore.languages.filter { testCase["preferredTarget"] == it.slug }.first(),
                            sourceLanguage = LanguageStore.languages.filter { testCase["preferredSource"] == it.slug }.first()
                    )
            )

            val expected = UserEntity()
            expected.id = input.id
            expected.setAudioHash(input.audioHash)
            expected.setAudioPath(input.audioPath)
            val expectedUserPreferences = UserPreferencesEntity()
            expectedUserPreferences.id = input.userPreferences.id
            expectedUserPreferences.setSourceLanguageId(input.userPreferences.sourceLanguage.id)
            expectedUserPreferences.setTargetLanguageId(input.userPreferences.targetLanguage.id)
            expected.setUserPreferencesEntity(expectedUserPreferences)

            val result = UserMapper(mockUserLanguageRepo, mockLanguageDao).mapToEntity(input)
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