package persistence.mapping

import data.DayNight
import data.UserPreferences
import io.reactivex.Observable
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito
import org.mockito.Mockito
import persistence.data.LanguageStore
import persistence.model.UserPreferencesEntity
import persistence.repo.LanguageRepo

class UserPreferencesMapperTest {
    val mockLanguageDao = Mockito.mock(LanguageRepo::class.java)

    @Before
    fun setup() {
        BDDMockito.given(mockLanguageDao.getById(Mockito.anyInt())).will {
            Observable.just(LanguageStore.languages[it.getArgument(0)])
        }
    }

    @Test
    fun testIfUserPreferencesEntityCorrectlyMappedToUserPreferences() {
        val userPreferencesMapper = UserPreferencesMapper(mockLanguageDao)

        val inputEntity = UserPreferencesEntity()
        inputEntity.uiLanguagePreference = "en"
        inputEntity.dayNightMode = DayNight.NIGHT.ordinal
        inputEntity.preferredTargetLanguageId = 2
        inputEntity.preferredSourceLanguageId = 3

        val expected = UserPreferences(
                id = 0,
                dayNightMode = DayNight.NIGHT,
                preferredTargetLanguage = LanguageStore.languages[2],
                preferredSourceLanguage = LanguageStore.languages[3]
        )

        val result = userPreferencesMapper.mapFromEntity(inputEntity)

        Assert.assertEquals(expected, result)
    }

    @Test
    fun testIfUserPreferencesCorrectlyMappedToUserPreferencesEntity() {
        val userPreferencesMapper = UserPreferencesMapper(mockLanguageDao)

        val expectedEntity = UserPreferencesEntity()
        expectedEntity.uiLanguagePreference = "en"
        expectedEntity.dayNightMode = DayNight.NIGHT.ordinal
        expectedEntity.preferredTargetLanguageId = 2
        expectedEntity.preferredSourceLanguageId = 3

        val input = UserPreferences(
                id = 0,
                dayNightMode = DayNight.NIGHT,
                preferredTargetLanguage = LanguageStore.languages[expectedEntity.preferredTargetLanguageId - 1],
                preferredSourceLanguage = LanguageStore.languages[expectedEntity.preferredSourceLanguageId - 1]
        )

        val result = userPreferencesMapper.mapToEntity(input)

        Assert.assertEquals(expectedEntity, result)
    }
}