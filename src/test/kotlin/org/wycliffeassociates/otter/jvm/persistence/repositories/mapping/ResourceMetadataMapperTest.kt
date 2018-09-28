package org.wycliffeassociates.otter.jvm.persistence.repositories.mapping

import io.reactivex.Maybe
import io.reactivex.Single
import jooq.tables.pojos.DublinCoreEntity
import org.junit.*
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito
import org.wycliffeassociates.otter.common.data.model.ResourceMetadata
import org.wycliffeassociates.otter.jvm.persistence.TestDataStore
import org.wycliffeassociates.otter.jvm.persistence.repositories.LanguageRepository
import java.io.File
import java.time.ZonedDateTime

class ResourceMetadataMapperTest {
    val TEST_CASES = listOf(
        Pair(
                DublinCoreEntity(
                        0,
                        "rc0.2",
                        "Someone or Organization",
                        "One or two sentence description of the resource.",
                        "text/usfm",
                        "ulb",
                        "2015-12-17T00:00:00-05:00",
                        0,
                        "2015-12-22T12:01:30-05:00",
                        "Name of Publisher",
                        "Bible",
                        "book",
                        "Unlocked Literal Bible",
                        "3",
                        "/path/to/my/container"
                ),
                ResourceMetadata(
                        "rc0.2",
                        "Someone or Organization",
                        "One or two sentence description of the resource.",
                        "text/usfm",
                        "ulb",
                        ZonedDateTime.parse("1450328400000"),
                        TestDataStore.languages.first(),
                        ZonedDateTime.parse("1450328400000"),
                        "Name of Publisher",
                        "Bible",
                        "book",
                        "Unlocked Literal Bible",
                        "3",
                        File("/path/to/my/container"),
                        0
                )
        )
    )

    val mockLanguageDao = Mockito.mock(LanguageRepository::class.java)

    @Test
    fun testIfDublinCoreCorrectlyMappedToResourceMetadata() {
        Mockito
                .`when`(mockLanguageDao.getById(anyInt()))
                .then {
                    val id: Int = it.getArgument(0)
                    TestDataStore.languages[id].id = id
                    Maybe.just(TestDataStore.languages[id])
                }

        for (testCase in TEST_CASES) {
            val input = testCase.first
            val expected = testCase.second
            // Setup the language id
            expected.language.id = TestDataStore.languages.indexOf(expected.language)

            val result = ResourceMetadataMapper(mockLanguageDao)
                    .mapFromEntity(Single.just(input))
                    .blockingGet()
            Assert.assertEquals(expected, result)
        }
    }

    @Test
    fun testIfResourceMetadataCorrectlyMappedToDublinCore() {
        for (testCase in TEST_CASES) {
            val input = testCase.second
            val expected = testCase.first
            expected.languageFk = 10
            input.language.id = 10
            val result = ResourceMetadataMapper(mockLanguageDao)
                    .mapToEntity(Maybe.just(input))
                    .blockingGet()
            AssertJooq.assertEntityEquals(expected, result)
        }
    }

}