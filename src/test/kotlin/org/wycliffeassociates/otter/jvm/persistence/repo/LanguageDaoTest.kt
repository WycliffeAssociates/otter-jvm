package org.wycliffeassociates.otter.jvm.persistence.repo

import jooq.tables.daos.LanguageEntityDao
import org.jooq.Configuration
import org.junit.*
import org.wycliffeassociates.otter.jvm.persistence.JooqTestConfiguration
import org.wycliffeassociates.otter.jvm.persistence.TestDataStore
import org.wycliffeassociates.otter.jvm.persistence.mapping.LanguageMapper

class LanguageDaoTest {
    val config: Configuration
    val entityDao: LanguageEntityDao

    init {
        JooqTestConfiguration.deleteDatabase("test_content.sqlite")
        config =  JooqTestConfiguration.createDatabase("test_content.sqlite")
        entityDao = LanguageEntityDao(config)
    }

    @Test
    fun testSingleLanguageCRUD() {
        val testLanguage = TestDataStore.languages.first()
        val dao = LanguageDao(entityDao, LanguageMapper())

        // Assert insert and retrieve
        dao.insert(testLanguage).blockingGet()
        var retrieved = dao
                .getById(testLanguage.id)
                .toSingle()
                .blockingGet()
        Assert.assertEquals(testLanguage, retrieved)

        testLanguage.name = "Updated Name"
        testLanguage.anglicizedName = "New Anglicized Name"
        testLanguage.isGateway = !testLanguage.isGateway
        testLanguage.direction = "ttb"
        testLanguage.slug = "glenn"

        // Assert update
        dao.update(testLanguage).blockingAwait()
        retrieved = dao
                .getById(testLanguage.id)
                .toSingle()
                .blockingGet()
        Assert.assertEquals(testLanguage, retrieved)

        // Assert delete
        dao.delete(testLanguage).blockingAwait()
        dao
                .getById(testLanguage.id)
                .doOnSuccess {
                    Assert.fail()
                }
                .blockingGet() // fail if the language is found
    }

    @Test
    fun testSingleLanguageInsertAndRetrieveBySlug() {
        val testLanguage = TestDataStore.languages.first()
        val dao = LanguageDao(entityDao, LanguageMapper())

        // Assert insert and retrieve
        dao.insert(testLanguage).blockingGet()
        val retrieved = dao
                .getBySlug(testLanguage.slug)
                .toSingle()
                .blockingGet()
        Assert.assertEquals(testLanguage, retrieved)
        // Assert delete
        dao.delete(testLanguage).blockingAwait()
        dao
                .getById(testLanguage.id)
                .doOnSuccess {
                    Assert.fail()
                }
                .subscribe() // fail if the language is found
    }

    @Test
    fun testAllLanguagesInsertAndRetrieve() {
        val dao = LanguageDao(entityDao, LanguageMapper())
        TestDataStore.languages.forEach {
            dao.insert(it).blockingGet()
        }
        val retrieved = dao.getAll().blockingGet()
        Assert.assertTrue(retrieved.containsAll(TestDataStore.languages))
        // delete all languages
        TestDataStore.languages.forEach {
            dao.delete(it).blockingAwait()
        }
    }
}