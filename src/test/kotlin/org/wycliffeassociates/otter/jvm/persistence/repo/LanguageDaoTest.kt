package org.wycliffeassociates.otter.jvm.persistence.repo

import org.jooq.Configuration
import org.junit.*
import org.wycliffeassociates.otter.jvm.persistence.JooqTestConfiguration
import org.wycliffeassociates.otter.jvm.persistence.TestDataStore
import org.wycliffeassociates.otter.jvm.persistence.mapping.LanguageMapper

class LanguageDaoTest {
    companion object {
        var config: Configuration? = null

        @BeforeClass
        @JvmStatic
        fun setupAll() {
            config = JooqTestConfiguration.setup("test_content.sqlite")
        }
        @AfterClass
        @JvmStatic
        fun tearDownAll() {
            JooqTestConfiguration.tearDown("test_content.sqlite")
        }
    }

    @Test
    fun testSingleLanguageCRUD() {
        val testLanguage = TestDataStore.languages.first()
        val dao = DefaultLanguageDao(
                config ?: throw Exception("Database failed to configure"),
                LanguageMapper()
        )
        DaoTestCases.assertInsertAndRetrieveSingle(dao, testLanguage)
        testLanguage.name = "Updated Name"
        testLanguage.anglicizedName = "New Anglicized Name"
        testLanguage.isGateway = !testLanguage.isGateway
        testLanguage.isRtl = !testLanguage.isRtl
        testLanguage.slug = "glenn"
        DaoTestCases.assertUpdate(dao, testLanguage)
        DaoTestCases.assertDelete(dao, testLanguage)
    }

    @Test
    fun testAllLanguagesInsertAndRetrieve() {
        val dao = DefaultLanguageDao(
                config ?: throw Exception("Database failed to configure"),
                LanguageMapper()
        )
        DaoTestCases.assertInsertAndRetrieveAll(dao, TestDataStore.languages)
        // delete all languages
        TestDataStore.languages.forEach {
            dao.delete(it).blockingAwait()
        }
    }
}