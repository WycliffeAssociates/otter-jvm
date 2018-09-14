package org.wycliffeassociates.otter.jvm.persistence.mapping

import org.wycliffeassociates.otter.common.data.model.Language
import org.junit.Assert
import org.junit.Test
import jooq.tables.pojos.LanguageEntity

class LanguageMapperTest {

    val LANGUAGE_TABLE = listOf(
        mapOf(
            "name" to "Español",
            "slug" to "esp",
            "anglicizedName" to "Spanish",
            "gateway" to "true",
            "rtl" to "true"
        ),
        mapOf(
            "name" to "हिन्दी",
            "slug" to "hin",
            "anglicizedName" to "Hindi",
            "gateway" to "true",
            "rtl" to "false"
        )
    )

    @Test
    fun testIfLanguageEntityCorrectlyMappedToLanguage() {
        for (testCase in LANGUAGE_TABLE) {
            val input = LanguageEntity(
                    42,
                    testCase["slug"],
                    testCase["name"],
                    if (testCase["gateway"] == "true") 1 else 0,
                    testCase["anglicizedName"],
                    if (testCase["rtl"] == "true") 1 else 0
            )


            val expected = Language(
                    id = input.id,
                    slug = input.slug,
                    name = input.name,
                    isGateway = input.gateway == 1,
                    anglicizedName = input.anglicized,
                    isRtl = input.rtl == 1
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
                    id = 1,
                    slug = testCase["slug"].orEmpty(),
                    name = testCase["name"].orEmpty(),
                    anglicizedName = testCase["anglicizedName"].orEmpty(),
                    isGateway = (testCase["canBeSource"] == "true"),
                    isRtl = (testCase["rtl"] == "true")
            )

            val expected = LanguageEntity(
                    input.id,
                    input.slug,
                    input.name,
                    if (input.isGateway) 1 else 0,
                    input.anglicizedName,
                    if (input.isRtl) 1 else 0
            )

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