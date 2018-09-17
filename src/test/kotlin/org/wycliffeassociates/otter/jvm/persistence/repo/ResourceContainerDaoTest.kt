package org.wycliffeassociates.otter.jvm.persistence.repo

import org.jooq.Configuration
import org.junit.*
import org.wycliffeassociates.otter.jvm.persistence.JooqTestConfiguration
import org.wycliffeassociates.otter.jvm.persistence.TestLanguageStore
import org.wycliffeassociates.otter.jvm.persistence.mapping.LanguageMapper

class ResourceContainerDaoTest {
    companion object {
        var config: Configuration? = null

        @BeforeClass
        @JvmStatic
        fun setupAll() {
            config = JooqTestConfiguration.setup("test_content.sqlite")
            // Put the languages in the database
        }
        @AfterClass
        @JvmStatic
        fun tearDownAll() {
            JooqTestConfiguration.tearDown("test_content.sqlite")
        }
    }

    @Test
    fun testSingleResourceContainerCRUD() {
//        val testLanguage = TestLanguageStore.languages.first()
//        val dao = DefaultLanguageDao(
//                config ?: throw Exception("Database failed to configure"),
//                LanguageMapper()
//        )
//        DaoTestCases.assertInsertAndRetrieveSingle(dao, testLanguage)
//        testLanguage.name = "Updated Name"
//        testLanguage.anglicizedName = "New Anglicized Name"
//        testLanguage.isGateway = !testLanguage.isGateway
//        testLanguage.isRtl = !testLanguage.isRtl
//        testLanguage.slug = "glenn"
//        DaoTestCases.assertUpdate(dao, testLanguage)
//        DaoTestCases.assertDelete(dao, testLanguage)
    }

    @Test
    fun testAllResourceContainersInsertAndRetrieve() {
//        val dao = DefaultLanguageDao(
//                config ?: throw Exception("Database failed to configure"),
//                LanguageMapper()
//        )
//        DaoTestCases.assertInsertAndRetrieveAll(dao, TestLanguageStore.languages)
    }
}