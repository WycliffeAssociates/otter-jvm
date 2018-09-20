package org.wycliffeassociates.otter.jvm.persistence.repo

import jooq.tables.daos.DublinCoreEntityDao
import jooq.tables.daos.LanguageEntityDao
import jooq.tables.daos.RcLinkEntityDao
import org.jooq.Configuration
import org.junit.*
import org.wycliffeassociates.otter.jvm.persistence.JooqTestConfiguration
import org.wycliffeassociates.otter.jvm.persistence.TestDataStore
import org.wycliffeassociates.otter.jvm.persistence.mapping.LanguageMapper
import org.wycliffeassociates.otter.jvm.persistence.mapping.ResourceContainerMapper
import java.io.File
import java.util.*

class ResourceContainerDaoTest {
    val config: Configuration
    val languageDao: LanguageDao

    init {
        JooqTestConfiguration.deleteDatabase("test_content.sqlite")
        config = JooqTestConfiguration.createDatabase("test_content.sqlite")
        languageDao = LanguageDao(LanguageEntityDao(config), LanguageMapper())

        // Put all the languages in the database
        TestDataStore.languages.forEach { language ->
            language.id = 0
            languageDao
                    .insert(language)
                    .blockingGet()
        }

    }

    @Test
    fun testSingleResourceContainerCRUD() {
        val testRc = TestDataStore.resourceContainers.first()
        testRc.language = TestDataStore.languages.first()
        val dao = ResourceContainerDao(
                DublinCoreEntityDao(config),
                RcLinkEntityDao(config),
                ResourceContainerMapper(languageDao)
        )

        // Insert & Retrieve
        dao.insert(testRc).blockingGet()
        var retrieved = dao.getById(testRc.id).toSingle().blockingGet()
        Assert.assertEquals(testRc, retrieved)

        // Update the test rc
        testRc.conformsTo = "new spec"
        testRc.creator = "Matthew Russell"
        testRc.description = "A book by Matthew Russell, written completely in YAML."
        testRc.format = "application/yaml"
        testRc.identifier = "mbr"
        val newTime = Calendar.getInstance()
        newTime.time = Date(1000 * (Date().time / 1000))
        testRc.modified = newTime
        testRc.issued = newTime
        testRc.language = TestDataStore.languages.last()
        testRc.publisher = "Russell House"
        testRc.type = "electronic"
        testRc.title = "My Amazing Title"
        testRc.version = 22
        testRc.path = File("/updated/path/to/my/container")

        // Update & Retrieve
        dao.update(testRc).blockingAwait()
        retrieved = dao.getById(testRc.id).toSingle().blockingGet()
        Assert.assertEquals(testRc, retrieved)

        // Delete
        dao.delete(testRc).blockingAwait()
        dao
                .getById(testRc.id)
                .doOnSuccess {
                    Assert.fail("RC not deleted")
                }
                .blockingGet()
    }

    @Test
    fun testAllResourceContainersInsertAndRetrieve() {
        val dao = ResourceContainerDao(
                DublinCoreEntityDao(config),
                RcLinkEntityDao(config),
                ResourceContainerMapper(languageDao)
        )
        TestDataStore.resourceContainers.forEach {
            dao.insert(it).blockingGet()
        }
        val retrieved = dao.getAll().blockingGet()
        Assert.assertTrue(retrieved.containsAll(TestDataStore.resourceContainers))
        TestDataStore.resourceContainers.forEach {
            dao.delete(it).blockingAwait()
        }
    }

    @Test
    fun testAddAndRemoveLinks() {
        val dao = ResourceContainerDao(
                DublinCoreEntityDao(config),
                RcLinkEntityDao(config),
                ResourceContainerMapper(languageDao)
        )
        val testRc1 = TestDataStore.resourceContainers[0]
        val testRc2 = TestDataStore.resourceContainers[1]
        dao.insert(testRc1).blockingGet()
        dao.insert(testRc2).blockingGet()

        // Add the link
        dao.addLink(testRc1, testRc2).blockingAwait()

        // Check to make sure the link is really there
        var rc1Links = dao.getLinks(testRc1).blockingGet()
        var rc2Links = dao.getLinks(testRc2).blockingGet()
        Assert.assertEquals(1, rc1Links.size)
        Assert.assertTrue(rc1Links.contains(testRc2))
        Assert.assertEquals(1, rc2Links.size)
        Assert.assertTrue(rc2Links.contains(testRc1))

        // Remove the link
        dao.removeLink(testRc2, testRc1).blockingAwait()
        // Check if the link is really gone
        rc1Links = dao.getLinks(testRc1).blockingGet()
        rc2Links = dao.getLinks(testRc2).blockingGet()
        Assert.assertTrue(rc1Links.isEmpty())
        Assert.assertTrue(rc2Links.isEmpty())

        dao.delete(testRc1).blockingAwait()
        dao.delete(testRc2).blockingAwait()
    }
}