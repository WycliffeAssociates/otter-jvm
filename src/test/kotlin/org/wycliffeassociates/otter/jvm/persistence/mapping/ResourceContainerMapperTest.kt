package org.wycliffeassociates.otter.jvm.persistence.mapping

import io.reactivex.Observable
import jooq.tables.pojos.DublinCoreEntity
import org.wycliffeassociates.otter.common.data.model.Language
import org.junit.*
import org.wycliffeassociates.otter.common.data.dao.Dao
import org.wycliffeassociates.otter.common.data.model.ResourceContainer
import org.wycliffeassociates.otter.jvm.persistence.JooqTestConfiguration
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
                        1,
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
                            setupClass()
                            this
                        },
                        Language(
                                0, // id set on language dao insert
                                "en",
                                "English",
                                "English",
                                false,
                                true
                        ),
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

    companion object {
        var languageDao: Dao<Language>? = null

        @BeforeClass
        @JvmStatic
        fun setupClass() {
            // setup database here to minimize need to create and delete database
            val jooqConfig = JooqTestConfiguration.setup("test_content.sqlite")
            languageDao = DefaultLanguageDao(jooqConfig, LanguageMapper())
        }

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            JooqTestConfiguration.tearDown("test_content.sqlite")
        }
    }

    @Test
    fun testIfDublinCoreCorrectlyMappedToResourceContainer() {
        for (testCase in TEST_CASES) {
            val input = testCase.first
            val expected = testCase.second
            if (languageDao == null) Assert.fail("Language dao failed to initialize")
            languageDao?.let { languageDao ->
                // make sure language is in the database
                input.languageFk = languageDao.insert(expected.language).blockingFirst() ?: 0
                expected.language.id = input.languageFk
                val result = ResourceContainerMapper(languageDao)
                        .mapFromEntity(Observable.just(input))
                        .blockingFirst()
                Assert.assertEquals(expected, result)
            }
        }
    }

    @Test
    fun testIfResourceContainerCorrectlyMappedToDublinCore() {
        for (testCase in TEST_CASES) {
            val input = testCase.second
            val expected = testCase.first
            expected.languageFk = 10
            input.language.id = 10
            languageDao?.let { languageDao ->
                // no need to put language in database
                val result = ResourceContainerMapper(languageDao)
                        .mapToEntity(Observable.just(input))
                        .blockingFirst()
                AssertJooq.assertDublinCoreEntityEquals(expected, result)
            }
        }
    }

}