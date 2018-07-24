package persistence.mapping

import data.model.UserPreferences
import io.reactivex.Observable
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito
import org.mockito.Mockito
import persistence.data.LanguageStore
import persistence.model.UserPreferencesEntity
import persistence.repo.LanguageRepo

class UserPreferencesDoor43MapperTest {
    val mockLanguageDao = Mockito.mock(LanguageRepo::class.java)

    @Before
    fun setup() {
        BDDMockito.given(mockLanguageDao.getById(Mockito.anyInt())).will {
            Observable.just(LanguageStore.getById(it.getArgument(0)))
        }
    }

    @Test
    fun testIfUserPreferencesEntityCorrectlyMappedToUserPreferences() {
        val userPreferencesMapper = UserPreferencesMapper(mockLanguageDao)

        val inputEntity = UserPreferencesEntity()
        inputEntity.targetLanguageId = 2
        inputEntity.sourceLanguageId = 3

        val expected = UserPreferences(
                id = 0,
                targetLanguage = LanguageStore.getById(2),
                sourceLanguage = LanguageStore.getById(3)
        )

        val result = userPreferencesMapper.mapFromEntity(inputEntity)

        Assert.assertEquals(expected, result)
    }

    @Test
    fun testIfUserPreferencesCorrectlyMappedToUserPreferencesEntity() {
        val userPreferencesMapper = UserPreferencesMapper(mockLanguageDao)

        val expectedEntity = UserPreferencesEntity()
        expectedEntity.targetLanguageId = 2
        expectedEntity.sourceLanguageId = 3

        val input = UserPreferences(
                id = 0,
                targetLanguage = LanguageStore.getById(expectedEntity.targetLanguageId),
                sourceLanguage = LanguageStore.getById(expectedEntity.sourceLanguageId)
        )

        val result = userPreferencesMapper.mapToEntity(input)

        Assert.assertEquals(expectedEntity, result)
    }
}