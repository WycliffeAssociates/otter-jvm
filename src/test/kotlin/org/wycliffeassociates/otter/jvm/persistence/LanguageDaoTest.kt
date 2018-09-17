package org.wycliffeassociates.otter.jvm.persistence

import org.jooq.Configuration
import org.junit.*
import org.wycliffeassociates.otter.jvm.persistence.mapping.LanguageMapper
import org.wycliffeassociates.otter.jvm.persistence.repo.DefaultLanguageDao

class LanguageDaoTest {
    companion object {
        var config: Configuration? = null

        @BeforeClass
        @JvmStatic
        fun setupAll() {
            config = JooqTestSetup.setup("test_content.sqlite")
        }
        @AfterClass
        @JvmStatic
        fun tearDownAll() {
            JooqTestSetup.tearDown("test_content.sqlite")
        }
    }

    @Test
    fun testSingleLanguageCRUD() {
        val testLanguage = TestLanguageStore.languages.first()
        val dao = DefaultLanguageDao(
                LanguageDaoTest.config ?: throw Exception("Database failed to configure"),
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
                LanguageDaoTest.config ?: throw Exception("Database failed to configure"),
                LanguageMapper()
        )
        DaoTestCases.assertInsertAndRetrieveAll(dao, TestLanguageStore.languages)
    }
}