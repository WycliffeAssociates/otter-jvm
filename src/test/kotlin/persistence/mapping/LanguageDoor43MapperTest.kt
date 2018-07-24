package persistence.mapping

import data.model.Language
import org.junit.Assert
import org.junit.Test
import persistence.model.LanguageEntity
import java.util.*

class LanguageDoor43MapperTest {

    val LANGUAGE_TABLE = listOf(
            mapOf(
                    "name" to "Español",
                    "slug" to "esp",
                    "anglicizedName" to "Spanish",
                    "canBeSource" to "true"
            ),
            mapOf(
                    "name" to "हिन्दी",
                    "slug" to "hin",
                    "anglicizedName" to "Hindi",
                    "canBeSource" to "true"
            )
    )

    @Test
    fun testIfLanguageEntityCorrectlyMappedToLanguage() {
        for (testCase in LANGUAGE_TABLE) {
            val input = LanguageEntity()
            input.id = Random().nextInt()
            input.setName(testCase["name"])
            input.setSlug(testCase["slug"])
            input.setAnglicizedName(testCase["anglicizedName"])
            input.setGateway(testCase["canBeSource"] == "true")

            val expected = Language(
                    id = input.id,
                    slug = input.slug,
                    name = input.name,
                    anglicizedName = input.anglicizedName,
                    isGateway = input.gateway
            )

            val result = LanguageMapper().mapFromEntity(input)
            try {
                Assert.assertEquals(expected, result)
            } catch (e: AssertionError) {
                println("Input: ${input.name}")
                println("Result: ${result.name}")
                throw e
            }
        }
    }

    @Test
    fun testIfLanguageCorrectlyMappedToLanguageEntity() {
        for (testCase in LANGUAGE_TABLE) {
            val input = Language(
                    id = Random().nextInt(),
                    slug = testCase["slug"].orEmpty(),
                    name = testCase["name"].orEmpty(),
                    anglicizedName = testCase["anglicizedName"].orEmpty(),
                    isGateway = (testCase["canBeSource"] == "true")
            )

            val expected = LanguageEntity()
            expected.id = input.id
            expected.setName(input.name)
            expected.setSlug(input.slug)
            expected.setAnglicizedName(input.anglicizedName)
            expected.setGateway(input.isGateway)

            val result = LanguageMapper().mapToEntity(input)
            try {
                Assert.assertEquals(expected, result)
            } catch (e: AssertionError) {
                println("Input: ${input.name}")
                println("Result: ${result.name}")
                throw e
            }
        }
    }

}