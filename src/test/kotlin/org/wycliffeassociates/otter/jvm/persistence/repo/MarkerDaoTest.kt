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
    val markerDao: MarkerDao
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
        markerDao = MarkerDao(MarkerEntityDao(config), MarkerMapper())
        takeDao = TakeDao(TakeEntityDao(config), markerDao, TakeMapper(markerDao))
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
        theTake.markers = listOf()

        TestDataStore.markers.forEach {
            it.id = 0
        }
    }

    @Test
    fun testSingleMarkerCRUD() {
        val testMarker = TestDataStore.markers.first()
        // Insert and Retrieve
        markerDao.insertForTake(testMarker, theTake).blockingGet()
        var retrieved = markerDao.getById(testMarker.id).blockingGet()
        Assert.assertEquals(retrieved, testMarker)
        // Update
        testMarker.number = 39
        testMarker.position = 25839
        testMarker.label = "verse39"
        markerDao.update(testMarker).blockingAwait()
        retrieved = markerDao.getById(testMarker.id).blockingGet()
        Assert.assertEquals(retrieved, testMarker)
        // Delete
        markerDao.delete(testMarker).blockingAwait()
        markerDao
                .getById(testMarker.id)
                .doOnSuccess {
                    Assert.fail("Marker was not deleted")
                }
                .blockingGet()
    }

    @Test
    fun testGetByTake() {
        TestDataStore.markers.forEach {
            markerDao.insertForTake(it, theTake).blockingGet()
        }
        val retrieved = markerDao.getByTake(theTake).blockingGet()
        Assert.assertEquals(TestDataStore.markers.size, retrieved.size)
        Assert.assertTrue(retrieved.containsAll(TestDataStore.markers))

        // Delete
        TestDataStore.markers.forEach {
            markerDao.delete(it).blockingGet()
        }
    }

    @Test
    fun testGetAll() {
        TestDataStore.markers.forEach {
            markerDao.insertForTake(it, theTake).blockingGet()
        }
        val retrieved = markerDao.getAll().blockingGet()
        Assert.assertEquals(TestDataStore.markers.size, retrieved.size)
        Assert.assertTrue(retrieved.containsAll(TestDataStore.markers))

        // Delete
        TestDataStore.markers.forEach {
            markerDao.delete(it).blockingGet()
        }
    }
}