package org.wycliffeassociates.otter.jvm.persistence.repo

import org.jooq.Configuration
import org.junit.*
import org.wycliffeassociates.otter.common.data.dao.Dao
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.common.data.model.ResourceContainer
import org.wycliffeassociates.otter.jvm.persistence.JooqTestConfiguration
import org.wycliffeassociates.otter.jvm.persistence.TestDataStore
import org.wycliffeassociates.otter.jvm.persistence.mapping.CollectionMapper
import org.wycliffeassociates.otter.jvm.persistence.mapping.LanguageMapper
import org.wycliffeassociates.otter.jvm.persistence.mapping.ResourceContainerMapper
import java.io.File
import java.util.*

class CollectionDaoTest {
    companion object {
        var config: Configuration = JooqTestConfiguration.setup("test_content.sqlite")
        var languageDao: Dao<Language> = DefaultLanguageDao(config, LanguageMapper())
        var rcDao: Dao<ResourceContainer> = DefaultResourceContainerDao(config, ResourceContainerMapper(languageDao))


        @BeforeClass
        @JvmStatic
        fun setupAll() {
            // Put all the languages in the database
            TestDataStore.languages.forEach { language ->
                language.id = languageDao
                        .insert(language)
                        .blockingFirst()
            }
            // Put all the resource containers in the database
            TestDataStore.resourceContainers.forEach { rc ->
                rc.language = TestDataStore.languages.filter { rc.language.slug == it.slug }.first()
                rc.id = rcDao
                        .insert(rc)
                        .blockingFirst()
            }
        }

        @AfterClass
        @JvmStatic
        fun tearDownAll() {
            JooqTestConfiguration.tearDown("test_content.sqlite")
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
        val dao = DefaultCollectionDao(config, CollectionMapper(rcDao))
        val id = dao
                .insert(testCollection)
                .blockingFirst()
        testCollection.id = id
        var retrieved = dao
                .getById(id)
                .blockingFirst()
        Assert.assertEquals(testCollection, retrieved)
        testCollection.titleKey = "newTitle"
        testCollection.labelKey = "newLabel"
        testCollection.slug = "new-slug"
        testCollection.sort = 22
        DaoTestCases.assertUpdate(dao, testCollection)
        DaoTestCases.assertDelete(dao, testCollection)
    }

    @Test
    fun testSingleCollectionWithParentAndSourceCRUD() {
        val testCollection = TestDataStore.collections[0]
        val parentSource = TestDataStore.collections[1]
        val dao = DefaultCollectionDao(config, CollectionMapper(rcDao))
        // Put the parent/source into the database
        parentSource.id = dao
                .insert(parentSource)
                .blockingFirst()
        // Insert child
        val id = dao
                .insertRelated(testCollection, parentSource, parentSource)
                .blockingFirst()
        testCollection.id = id
        var retrieved = dao
                .getById(id)
                .blockingFirst()
        Assert.assertEquals(testCollection, retrieved)

        // Check the parent's children
        var childrenOfParent = dao.getChildren(parentSource).blockingFirst()
        Assert.assertEquals(1, childrenOfParent.size)
        Assert.assertTrue(childrenOfParent.contains(testCollection))

        // Check the source
        var retrievedSource = dao.getSource(testCollection).blockingFirst()
        Assert.assertEquals(parentSource, retrievedSource)

        // Update parent and source
        val newParentSource = TestDataStore.collections[2]
        newParentSource.id = dao.insert(newParentSource).blockingFirst()
        dao.setParent(testCollection, newParentSource).blockingAwait()
        dao.setSource(testCollection, newParentSource).blockingAwait()

        // Check the new parent's children
        childrenOfParent = dao.getChildren(newParentSource).blockingFirst()
        Assert.assertEquals(1, childrenOfParent.size)
        Assert.assertTrue(childrenOfParent.contains(testCollection))

        // Check the new source
        retrievedSource = dao.getSource(testCollection).blockingFirst()
        Assert.assertEquals(newParentSource, retrievedSource)

        // Clean up
        DaoTestCases.assertDelete(dao, testCollection)
        DaoTestCases.assertDelete(dao, parentSource)
        DaoTestCases.assertDelete(dao, newParentSource)
    }

    @Test
    fun testSingleCollectionWithParentAndSourceRemoveRelativesCRUD() {
        val testCollection = TestDataStore.collections[0]
        val parentSource = TestDataStore.collections[1]
        val dao = DefaultCollectionDao(config, CollectionMapper(rcDao))
        // Put the parent/source into the database
        parentSource.id = dao
                .insert(parentSource)
                .blockingFirst()
        // Insert child
        val id = dao
                .insertRelated(testCollection, parentSource, parentSource)
                .blockingFirst()
        testCollection.id = id
        var retrieved = dao
                .getById(id)
                .blockingFirst()
        Assert.assertEquals(testCollection, retrieved)

        // Remove parent and source
        dao.setParent(testCollection, null).blockingAwait()
        dao.setSource(testCollection, null).blockingAwait()

        // Check the parent's children
        val childrenOfParent = dao.getChildren(parentSource).blockingFirst()
        Assert.assertEquals(0, childrenOfParent.size)

        // Check the new source
        val retrievedSource = dao.getSource(testCollection)
        Assert.assertTrue(retrievedSource.isEmpty.blockingGet())

        // Clean up
        DaoTestCases.assertDelete(dao, testCollection)
        DaoTestCases.assertDelete(dao, parentSource)
    }

    @Test
    fun testAllCollectionsInsertAndRetrieve() {
        val dao = DefaultCollectionDao(config, CollectionMapper(rcDao))
        DaoTestCases.assertInsertAndRetrieveAll(dao, TestDataStore.collections)
        TestDataStore.collections.forEach {
            dao.delete(it).blockingAwait()
        }
    }
}