package org.wycliffeassociates.otter.jvm.persistence.repo

import jooq.tables.daos.*
import org.apache.batik.gvt.Marker
import org.jooq.Configuration
import org.junit.*
import org.wycliffeassociates.otter.jvm.persistence.JooqTestConfiguration
import org.wycliffeassociates.otter.jvm.persistence.TestDataStore
import org.wycliffeassociates.otter.jvm.persistence.mapping.*
import java.io.File
import java.util.*

// These (integration) tests are combined since the two daos depend on each other (because of selected take).
// If selected take for chunk was a function in the take dao instead of a field in chunk, this could be avoided.
class ChunkDaoAndTakeDaoTest {
    val config: Configuration
    val languageDao: LanguageDao
    val rcDao: ResourceContainerDao
    val collectionDao: CollectionDao
    val markerDao: MarkerDao
    val takeDao: TakeDao
    val chunkDao: ChunkDao

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
    }

    @Test
    fun testSingleChunkWithoutAndWithTakeCRUD() {
        val theCollection = TestDataStore.collections.first()

        val testChunk = TestDataStore.chunks.first()
        testChunk.selectedTake = null
        testChunk.id = 0

        // Insert & Retrieve (Both take and chunk)
        chunkDao.insertForCollection(testChunk, theCollection).blockingGet()
        var retrieved = chunkDao.getById(testChunk.id).toSingle().blockingGet()
        Assert.assertEquals(testChunk, retrieved)

        // Update the test chunk and add a selected take
        testChunk.selectedTake = null
        testChunk.start = 32
        testChunk.end = 32
        testChunk.labelKey = "chunk32"
        testChunk.sort = 31
        val testTake = TestDataStore.takes.first()
        testTake.id = 0
        // Make sure the necessary takes are in the dao
        takeDao.insertForChunk(testTake, testChunk).blockingGet()
        testChunk.selectedTake = testTake

        // Update & Retrieve
        chunkDao.update(testChunk).blockingAwait()
        retrieved = chunkDao.getById(testChunk.id).toSingle().blockingGet()
        Assert.assertEquals(testChunk, retrieved)

        // Delete
        collectionDao.delete(theCollection).blockingAwait()
        // Chunk should be deleted with collection
        chunkDao
                .getById(testChunk.id)
                .doOnSuccess {
                    Assert.fail("Chunk not deleted")
                }
                .blockingGet()

        // Take should be deleted with chunk
        takeDao
                .getById(testTake.id)
                .doOnSuccess {
                    Assert.fail("Take not deleted")
                }
                .blockingGet()
        // Add back the collection to not affect other tests
        theCollection.id = 0
        collectionDao.insert(theCollection).blockingGet()
    }

    @Test
    fun testSingleTakeCRUD() {
        val theCollection = TestDataStore.collections.first()
        val theChunk = TestDataStore.chunks.first()
        theChunk.id = 0
        theChunk.selectedTake = null
        chunkDao.insertForCollection(theChunk, theCollection).blockingGet()
        val testTake = TestDataStore.takes.first()
        testTake.id = 0

        // Insert & Retrieve take
        takeDao.insertForChunk(testTake, theChunk).blockingGet()
        var retrieved = takeDao.getById(testTake.id).toSingle().blockingGet()
        Assert.assertEquals(testTake, retrieved)

        // Update the test take
        testTake.path = File("new/take/path")
        testTake.number = 1965
        testTake.isUnheard = !testTake.isUnheard
        testTake.filename = "newfilename.wav"
        testTake.markers = listOf()
        testTake.timestamp = Calendar.getInstance().apply {
            timeInMillis = time.time * 1000
        }

        // Update & Retrieve
        takeDao.update(testTake).blockingAwait()
        retrieved = takeDao.getById(testTake.id).blockingGet()
        Assert.assertEquals(testTake, retrieved)

        // Delete
        takeDao.delete(testTake).blockingAwait()
        // Take should be deleted
        takeDao
                .getById(testTake.id)
                .doOnSuccess {
                    Assert.fail("Take not deleted")
                }
                .blockingGet()
        // Delete the chunk
        chunkDao.delete(theChunk).blockingAwait()
    }

    @Test
    fun testGetTakesByChunk() {
        val theCollection = TestDataStore.collections.first()
        val testChunk = TestDataStore.chunks.first()
        testChunk.id = 0
        testChunk.selectedTake = null

        chunkDao.insertForCollection(testChunk, theCollection).blockingGet()
        // Put all the takes in the chunk
        TestDataStore.takes.forEach {
            takeDao.insertForChunk(it, testChunk).blockingGet()
        }

        val retrievedTakes = takeDao.getByChunk(testChunk).blockingGet()
        Assert.assertTrue(retrievedTakes.containsAll(TestDataStore.takes))
        Assert.assertEquals(TestDataStore.takes.size, retrievedTakes.size)

        chunkDao.delete(testChunk).blockingGet()
    }

    @Test
    fun testGetChunksByCollection() {
        val theCollection = TestDataStore.collections.first()
        TestDataStore.chunks.forEach {
            it.id = 0
            it.selectedTake = null
            chunkDao.insertForCollection(it, theCollection).blockingGet()
        }

        val retrievedChunks = chunkDao.getByCollection(theCollection).blockingGet()
        Assert.assertTrue(retrievedChunks.containsAll(TestDataStore.chunks))
        Assert.assertEquals(TestDataStore.chunks.size, retrievedChunks.size)

        TestDataStore.chunks.forEach {
            chunkDao.delete(it).blockingGet()
        }
    }

    @Test
    fun testGetAllChunks() {
        val theCollection = TestDataStore.collections.first()
        TestDataStore.chunks.forEach {
            it.id = 0
            it.selectedTake = null
            chunkDao.insertForCollection(it, theCollection).blockingGet()
        }

        val retrievedChunks = chunkDao.getAll().blockingGet()
        Assert.assertTrue(retrievedChunks.containsAll(TestDataStore.chunks))
        Assert.assertEquals(TestDataStore.chunks.size, retrievedChunks.size)

        // Clean up
        TestDataStore.chunks.forEach {
            chunkDao.delete(it).blockingGet()
        }
    }

    @Test
    fun testGetAllTakes() {
        val theCollection = TestDataStore.collections.first()
        val theChunk = TestDataStore.chunks.first()
        theChunk.id = 0
        theChunk.selectedTake = null
        chunkDao.insertForCollection(theChunk, theCollection).blockingGet()
        TestDataStore.takes.forEach {
            it.id = 0
            takeDao.insertForChunk(it, theChunk).blockingGet()
        }

        val retrievedTakes = takeDao.getAll().blockingGet()
        Assert.assertTrue(retrievedTakes.containsAll(TestDataStore.takes))
        Assert.assertEquals(TestDataStore.takes.size, retrievedTakes.size)

        // Clean up
        chunkDao.delete(theChunk).blockingGet()
    }
}