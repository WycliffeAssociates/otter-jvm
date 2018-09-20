package org.wycliffeassociates.otter.jvm.persistence.repo

import jooq.tables.daos.CollectionEntityDao
import jooq.tables.daos.DublinCoreEntityDao
import jooq.tables.daos.LanguageEntityDao
import jooq.tables.daos.RcLinkEntityDao
import org.jooq.Configuration
import org.junit.*
import org.wycliffeassociates.otter.jvm.persistence.JooqTestConfiguration
import org.wycliffeassociates.otter.jvm.persistence.TestDataStore
import org.wycliffeassociates.otter.jvm.persistence.mapping.CollectionMapper
import org.wycliffeassociates.otter.jvm.persistence.mapping.LanguageMapper
import org.wycliffeassociates.otter.jvm.persistence.mapping.ResourceContainerMapper

class CollectionDaoTest {
    val config: Configuration
    val languageDao: LanguageDao
    val rcDao: ResourceContainerDao

    init {
        JooqTestConfiguration.deleteDatabase("test_content.sqlite")
        config = JooqTestConfiguration.createDatabase("test_content.sqlite")
        languageDao = LanguageDao(LanguageEntityDao(config), LanguageMapper())
        rcDao = ResourceContainerDao(
                DublinCoreEntityDao(config),
                RcLinkEntityDao(config),
                ResourceContainerMapper(languageDao)
        )
        // Put all the languages in the database
        TestDataStore.languages.forEach { language ->
            language.id = 0
            languageDao.insert(language).blockingGet()
        }
        // Put all the resource containers in the database
        TestDataStore.resourceContainers.forEach { rc ->
            rc.id = 0
            rcDao.insert(rc).blockingGet()
        }
    }

    @Before
    fun setup() {
        TestDataStore.collections.forEach {
            it.id = 0
        }
    }

    @Test
    fun testSingleCollectionNoParentNoSourceCRUD() {
        val testCollection = TestDataStore.collections.first()
        val dao = CollectionDao(CollectionEntityDao(config), CollectionMapper(rcDao))
        // Insert & Retrieve
        val id = dao
                .insert(testCollection)
                .blockingGet()
        testCollection.id = id
        var retrieved = dao
                .getById(id)
                .blockingGet()
        Assert.assertEquals(testCollection, retrieved)
        // Update & Retrieve
        testCollection.titleKey = "newTitle"
        testCollection.labelKey = "newLabel"
        testCollection.slug = "new-slug"
        testCollection.sort = 22
        dao.update(testCollection).blockingAwait()
        retrieved = dao.getById(testCollection.id).blockingGet()
        Assert.assertEquals(testCollection, retrieved)
        // Delete
        dao.delete(testCollection).blockingAwait()
        dao
                .getById(testCollection.id)
                .doOnSuccess {
                    Assert.fail("Collection not deleted")
                }
                .blockingGet()
    }

    @Test
    fun testSingleCollectionWithParentAndSourceCRUD() {
        val testCollection = TestDataStore.collections[0]
        val parentSource = TestDataStore.collections[1]
        val dao = CollectionDao(CollectionEntityDao(config), CollectionMapper(rcDao))
        // Put the parent/source into the database
        dao
                .insert(parentSource)
                .blockingGet()
        // Insert child
        dao
                .insertRelated(testCollection, parentSource, parentSource)
                .blockingGet()
        val retrieved = dao
                .getById(testCollection.id)
                .toSingle()
                .blockingGet()
        Assert.assertEquals(testCollection, retrieved)

        // Check the parent's children
        var childrenOfParent = dao.getChildren(parentSource).blockingGet()
        Assert.assertEquals(1, childrenOfParent.size)
        Assert.assertTrue(childrenOfParent.contains(testCollection))

        // Check the source
        var retrievedSource = dao.getSource(testCollection).blockingGet()
        Assert.assertEquals(parentSource, retrievedSource)

        // Update parent and source
        val newParentSource = TestDataStore.collections[2]
        dao.insert(newParentSource).blockingGet()
        dao.setParent(testCollection, newParentSource).blockingAwait()
        dao.setSource(testCollection, newParentSource).blockingAwait()

        // Check the new parent's children
        childrenOfParent = dao.getChildren(newParentSource).blockingGet()
        Assert.assertEquals(1, childrenOfParent.size)
        Assert.assertTrue(childrenOfParent.contains(testCollection))

        // Check the new source
        retrievedSource = dao.getSource(testCollection).blockingGet()
        Assert.assertEquals(newParentSource, retrievedSource)

        // Clean up
        dao.delete(testCollection).blockingAwait()
        dao.delete(parentSource).blockingAwait()
        dao.delete(newParentSource).blockingAwait()
    }

    @Test
    fun testSingleCollectionWithParentAndSourceRemoveRelativesCRUD() {
        val testCollection = TestDataStore.collections[0]
        val parentSource = TestDataStore.collections[1]
        val dao = CollectionDao(CollectionEntityDao(config), CollectionMapper(rcDao))
        // Put the parent/source into the database
        dao.insert(parentSource).blockingGet()
        // Insert child
        dao.insertRelated(testCollection, parentSource, parentSource).blockingGet()
        val retrieved = dao.getById(testCollection.id).toSingle().blockingGet()
        Assert.assertEquals(testCollection, retrieved)

        // Remove parent and source
        dao.setParent(testCollection, null).blockingAwait()
        dao.setSource(testCollection, null).blockingAwait()

        // Check the parent's children
        val childrenOfParent = dao.getChildren(parentSource).blockingGet()
        Assert.assertEquals(0, childrenOfParent.size)

        // Check the new source
        val retrievedSource = dao.getSource(testCollection)
        Assert.assertTrue(retrievedSource.isEmpty.blockingGet())

        // Clean up
        dao.delete(testCollection).blockingAwait()
        dao.delete(parentSource).blockingAwait()
    }

    @Test
    fun testAllCollectionsInsertAndRetrieve() {
        val dao = CollectionDao(CollectionEntityDao(config), CollectionMapper(rcDao))
        TestDataStore.collections.forEach {
            dao.insert(it).blockingGet()
        }
        val retrieved = dao.getAll().blockingGet()
        println(TestDataStore.collections.sortedBy { it.id })
        println(retrieved.sortedBy { it.id })
        Assert.assertTrue(retrieved.containsAll(TestDataStore.collections))
        TestDataStore.collections.forEach {
            dao.delete(it).blockingAwait()
        }
    }

    @Test
    fun testGetProjects() {
        val dao = CollectionDao(CollectionEntityDao(config), CollectionMapper(rcDao))
        val parent = TestDataStore.collections[0]
        val project = TestDataStore.collections[1]
        val notAProject = TestDataStore.collections[2]
        dao.insert(parent).blockingGet()
        dao.insertRelated(project, null, parent).blockingGet()
        dao.insertRelated(notAProject, parent, null).blockingGet()

        // Try to get the projects and nothing else
        val retrieved = dao.getProjects().blockingGet()
        Assert.assertEquals(1, retrieved.size)
        Assert.assertTrue(retrieved.contains(project))

        dao.delete(notAProject).blockingGet()
        dao.delete(project).blockingGet()
        dao.delete(parent).blockingGet()
    }

    @Test
    fun testGetSources() {
        val dao = CollectionDao(CollectionEntityDao(config), CollectionMapper(rcDao))
        val source = TestDataStore.collections[0]
        val project = TestDataStore.collections[1]
        val notAProject = TestDataStore.collections[2]
        dao.insert(source).blockingGet()
        dao.insertRelated(project, null, source).blockingGet()
        dao.insertRelated(notAProject, source, null).blockingGet()

        // Try to get the root sources and nothing else
        val retrieved = dao.getSources().blockingGet()
        Assert.assertEquals(1, retrieved.size)
        Assert.assertTrue(retrieved.contains(source))

        dao.delete(notAProject).blockingGet()
        dao.delete(project).blockingGet()
        dao.delete(source).blockingGet()
    }
}