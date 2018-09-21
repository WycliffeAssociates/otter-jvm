package org.wycliffeassociates.otter.jvm.persistence.repo

import jooq.tables.daos.*
import org.jooq.Configuration
import org.junit.Assert
import org.junit.Test
import org.wycliffeassociates.otter.common.data.model.Take
import org.wycliffeassociates.otter.jvm.persistence.JooqTestConfiguration
import org.wycliffeassociates.otter.jvm.persistence.TestDataStore
import org.wycliffeassociates.otter.jvm.persistence.mapping.*

class MarkerDaoTest {
    val config: Configuration
    val languageDao: LanguageDao
    val rcDao: ResourceContainerDao
    val collectionDao: CollectionDao
    val takeDao: TakeDao
    val chunkDao: ChunkDao
    val theTake: Take

    init {
        JooqTestConfiguration.deleteDatabase("test_content.sqlite")
        config = JooqTestConfiguration.createDatabase("test_content.sqlite")
        languageDao = LanguageDao(LanguageEntityDao(config), LanguageMapper())
        rcDao = ResourceContainerDao(
                DublinCoreEntityDao(config),
                RcLinkEntityDao(config),
                ResourceContainerMapper(languageDao)
        )
        collectionDao = CollectionDao(CollectionEntityDao(config), CollectionMapper(rcDao))
        takeDao = TakeDao(TakeEntityDao(config), TakeMapper())
        chunkDao = ChunkDao(ContentEntityDao(config), ChunkMapper(takeDao))

        // Put all the languages in the database
        TestDataStore.languages.forEach { language ->
            language.id = 0
            languageDao
                    .insert(language)
                    .blockingGet()
        }
        // Put all the resource containers in the database
        TestDataStore.resourceContainers.forEach { rc ->
            rc.id = 0
            rcDao
                    .insert(rc)
                    .blockingGet()
        }
        // Put all the collections in the database
        TestDataStore.collections.forEach { collection ->
            collection.id = 0
            collectionDao
                    .insert(collection)
                    .blockingGet()
        }

        chunkDao.insertForCollection(TestDataStore.chunks.first(), TestDataStore.collections.first()).blockingGet()
        takeDao.insertForChunk(TestDataStore.takes.first(), TestDataStore.chunks.first()).blockingGet()
        theTake = TestDataStore.takes.first()

        TestDataStore.markers.forEach {
            it.id = 0
        }
    }

    @Test
    fun testSingleMarkerCRUD() {
        val dao = MarkerDao(MarkerEntityDao(config), MarkerMapper())
        val testMarker = TestDataStore.markers.first()
        // Insert and Retrieve
        dao.insertForTake(testMarker, theTake).blockingGet()
        var retrieved = dao.getById(testMarker.id).blockingGet()
        Assert.assertEquals(retrieved, testMarker)
        // Update
        testMarker.number = 39
        testMarker.position = 25839
        testMarker.label = "verse39"
        dao.update(testMarker).blockingAwait()
        retrieved = dao.getById(testMarker.id).blockingGet()
        Assert.assertEquals(retrieved, testMarker)
        // Delete
        dao.delete(testMarker).blockingAwait()
        dao
                .getById(testMarker.id)
                .doOnSuccess {
                    Assert.fail("Marker was not deleted")
                }
                .blockingGet()
    }

    @Test
    fun testGetByTake() {
        val dao = MarkerDao(MarkerEntityDao(config), MarkerMapper())
        TestDataStore.markers.forEach {
            dao.insertForTake(it, theTake).blockingGet()
        }
        val retrieved = dao.getByTake(theTake).blockingGet()
        Assert.assertEquals(TestDataStore.markers.size, retrieved.size)
        Assert.assertTrue(retrieved.containsAll(TestDataStore.markers))

        // Delete
        TestDataStore.markers.forEach {
            dao.delete(it).blockingGet()
        }
    }

    @Test
    fun testGetAll() {
        val dao = MarkerDao(MarkerEntityDao(config), MarkerMapper())
        TestDataStore.markers.forEach {
            dao.insertForTake(it, theTake).blockingGet()
        }
        val retrieved = dao.getAll().blockingGet()
        Assert.assertEquals(TestDataStore.markers.size, retrieved.size)
        Assert.assertTrue(retrieved.containsAll(TestDataStore.markers))

        // Delete
        TestDataStore.markers.forEach {
            dao.delete(it).blockingGet()
        }
    }
}