package org.wycliffeassociates.otter.jvm.persistence.mapping

import io.reactivex.Observable
import jooq.tables.pojos.DublinCoreEntity
import org.wycliffeassociates.otter.common.data.model.Language
import org.junit.*
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito
import org.wycliffeassociates.otter.common.data.dao.Dao
import org.wycliffeassociates.otter.common.data.model.ResourceContainer
import org.wycliffeassociates.otter.jvm.persistence.JooqTestConfiguration
import org.wycliffeassociates.otter.jvm.persistence.TestDataStore
import org.wycliffeassociates.otter.jvm.persistence.repo.DefaultLanguageDao
import java.io.File
import java.util.*

class ResourceContainerMapperTest {
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
                        3,
                        "/path/to/my/container"
                ),
                ResourceContainer(
                        0,
                        "rc0.2",
                        "Someone or Organization",
                        "One or two sentence description of the resource.",
                        "text/usfm",
                        "ulb",
                        with(Calendar.getInstance()) {
                            time = Date(1450328400000)
                            this
                        },
                        TestDataStore.languages.first(),
                        with(Calendar.getInstance()) {
                            time = Date(1450803690000)
                            this
                        },
                        "Name of Publisher",
                        "Bible",
                        "book",
                        "Unlocked Literal Bible",
                        3,
                        File("/path/to/my/container")
                )
        )
    )

    val mockLanguageDao = Mockito.mock(DefaultLanguageDao::class.java)

    @Test
    fun testIfDublinCoreCorrectlyMappedToResourceContainer() {
        Mockito
                .`when`(mockLanguageDao.getById(anyInt()))
                .then {
                    val id: Int = it.getArgument(0)
                    TestDataStore.languages[id].id = id
                    Observable.just(TestDataStore.languages[id])
                }

        for (testCase in TEST_CASES) {
            val input = testCase.first
            val expected = testCase.second
            // Setup the language id
            expected.language.id = TestDataStore.languages.indexOf(expected.language)

            val result = ResourceContainerMapper(mockLanguageDao)
                    .mapFromEntity(Observable.just(input))
                    .blockingFirst()
            Assert.assertEquals(expected, result)
        }
    }

    @Test
    fun testIfResourceContainerCorrectlyMappedToDublinCore() {
        for (testCase in TEST_CASES) {
            val input = testCase.second
            val expected = testCase.first
            expected.languageFk = 10
            input.language.id = 10
            val result = ResourceContainerMapper(mockLanguageDao)
                    .mapToEntity(Observable.just(input))
                    .blockingFirst()
            AssertJooq.assertDublinCoreEntityEquals(expected, result)
        }
    }

}