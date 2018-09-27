package org.wycliffeassociates.otter.jvm.persistence.repo

import io.reactivex.Maybe
import io.reactivex.Single
import jooq.tables.daos.ContentDerivativeDao
import jooq.tables.daos.ContentEntityDao
import jooq.tables.daos.ResourceLinkDao
import jooq.tables.pojos.ContentEntity
import jooq.tables.pojos.TakeEntity
import org.junit.*
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito
import org.wycliffeassociates.otter.common.data.model.Chunk
import org.wycliffeassociates.otter.jvm.persistence.TestDataStore
import org.wycliffeassociates.otter.jvm.persistence.mapping.ChunkMapper
import org.wycliffeassociates.otter.common.data.model.Collection


class ChunkDaoTest {
    val mockEntityDao: ContentEntityDao = Mockito.mock(ContentEntityDao::class.java)
    val mockMapper: ChunkMapper = Mockito.mock(ChunkMapper::class.java)
    val mockResourceDao: ResourceLinkDao = Mockito.mock(ResourceLinkDao::class.java)
    val mockDerivativeDao: ContentDerivativeDao = Mockito.mock(ContentDerivativeDao::class.java)
    val dao = ChunkDao(mockEntityDao, mockDerivativeDao, mockResourceDao, mockMapper)
    // Required in Kotlin to use Mockito any() argument matcher
    fun <T> helperAny(): T = ArgumentMatchers.any()

    @Test
    fun testDelete() {
        var entityDeleteByIdWasCalled = false
        var deletedId = 0

        TestDataStore.chunks.forEach { chunks ->
            Mockito
                    .`when`(mockEntityDao.deleteById(anyInt()))
                    .thenAnswer {
                        entityDeleteByIdWasCalled = true
                        deletedId = it.getArgument(0)
                        null
                    }
            // Reset
            entityDeleteByIdWasCalled = false
            deletedId = -1
            chunks.id = TestDataStore.chunks.indexOf(chunks)

            dao.delete(chunks).blockingAwait()

            Assert.assertTrue(entityDeleteByIdWasCalled)
            Assert.assertEquals(chunks.id, deletedId)
        }
    }

    @Test
    fun testGetAll() {
        Mockito
                .`when`(mockEntityDao.findAll())
                .thenReturn(TestDataStore.chunks.map {
                    val entity = ContentEntity()
                    entity.id = TestDataStore.chunks.indexOf(it)
                    entity
                })
        Mockito
                .`when`(mockMapper.mapFromEntity(helperAny()))
                .then {
                    val entity = it.getArgument<Single<ContentEntity>>(0).blockingGet()
                    Maybe.just(TestDataStore.chunks[entity.id])
                }

        val retrieved = dao.getAll().blockingGet()

        Assert.assertEquals(TestDataStore.chunks.size, retrieved.size)
        Assert.assertTrue(retrieved.containsAll(TestDataStore.chunks))
    }

    @Test
    fun testGetById() {
        Mockito
                .`when`(mockEntityDao.fetchOneById(anyInt()))
                .then {
                    val entity = ContentEntity()
                    entity.id = it.getArgument(0)
                    entity
                }
        Mockito
                .`when`(mockMapper.mapFromEntity(helperAny()))
                .then {
                    val entity = it.getArgument<Single<ContentEntity>>(0).blockingGet()
                    Maybe.just(TestDataStore.chunks[entity.id])
                }

        TestDataStore.chunks.forEach {
            val retrieved = dao.getById(TestDataStore.chunks.indexOf(it)).blockingGet()
            Assert.assertEquals(it, retrieved)
        }
    }

    @Test
    fun testGetByNonExistentId() {
        Mockito
                .`when`(mockEntityDao.fetchOneById(anyInt()))
                .thenThrow(RuntimeException::class.java)

        TestDataStore.chunks.forEach {
            dao
                    .getById(TestDataStore.chunks.indexOf(it))
                    .doOnSuccess {
                        Assert.fail()
                    }.blockingGet()
        }
    }

    @Test
    fun testGetByCollection() {
        val testCollection = Collection(
                0,
                "",
                "",
                "",
                TestDataStore.resourceContainers.first(),
                4
        )
        var correctCollectionFk = false
        Mockito
                .`when`(mockEntityDao.fetchByCollectionFk(anyInt()))
                .then {
                    if (it.getArgument<Int>(0) == testCollection.id) correctCollectionFk = true
                    TestDataStore.chunks.map { take ->
                        val entity = ContentEntity()
                        entity.collectionFk = it.getArgument(0)
                        entity.id = TestDataStore.chunks.indexOf(take)
                        entity
                    }
                }
        Mockito
                .`when`(mockMapper.mapFromEntity(helperAny()))
                .then {
                    val entity = it.getArgument<Single<ContentEntity>>(0).blockingGet()
                    Maybe.just(TestDataStore.chunks[entity.id])
                }

        correctCollectionFk = false
        val retrieved = dao.getByCollection(testCollection).blockingGet()
        Assert.assertTrue(correctCollectionFk)
        Assert.assertEquals(TestDataStore.chunks.size, retrieved.size)
        Assert.assertTrue(retrieved.containsAll(TestDataStore.chunks))
    }

    @Test
    fun testInsertChunkForCollection() {
        val testCollection = Collection(
                0,
                "",
                "",
                "",
                TestDataStore.resourceContainers.first(),
                4
        )
        val theChunk = TestDataStore.chunks[0]
        theChunk.selectedTake = TestDataStore.takes[0].apply { id = 2 }
        val tmpStore = mutableListOf<ContentEntity>()

        var insertedCollectionFk = 0
        var idWasNull = false
        var selectedTakeFk = 0

        Mockito
                .`when`(mockEntityDao.insert(helperAny<ContentEntity>()))
                .then {
                    val entity: ContentEntity = it.getArgument(0)
                    // if input id was 0, it should have been replaced with null
                    if (entity.id == null) idWasNull = true
                    selectedTakeFk = entity.selectedTakeFk
                    insertedCollectionFk = entity.collectionFk
                    // put in tmpStore
                    entity.id = tmpStore.size + 1
                    tmpStore.add(entity)
                    Unit
                }
        Mockito
                .`when`(mockEntityDao.findAll())
                .then {
                    tmpStore
                }
        Mockito
                .`when`(mockMapper.mapFromEntity(helperAny()))
                .then {
                    val entity: TakeEntity = it.getArgument(0)
                    // Just return a value. Not critical for the test
                    Maybe.just(TestDataStore.takes[entity.id])
                }
        Mockito
                .`when`(mockMapper.mapToEntity(helperAny()))
                .then {
                    val chunk = it.getArgument<Maybe<Chunk>>(0).blockingGet()
                    val entity = ContentEntity()
                    entity.id = chunk.id
                    entity.selectedTakeFk = chunk.selectedTake?.id
                    Single.just(entity)
                }

        theChunk.id = 0
        insertedCollectionFk = 0
        idWasNull = false
        selectedTakeFk = 0
        tmpStore.clear()
        // Add a dummy take to the store
        tmpStore.apply {
            val entity = ContentEntity()
            entity.id = 1
            add(entity)
        }
        val id = dao.insertForCollection(theChunk, testCollection).blockingGet()
        Assert.assertEquals(testCollection.id, insertedCollectionFk)
        Assert.assertTrue(idWasNull)
        Assert.assertEquals(theChunk.selectedTake?.id, selectedTakeFk)
        Assert.assertEquals(id, tmpStore.size)
    }

    @Test
    fun testUpdateChunkSameCollection() {
        val updatedChunk = TestDataStore.chunks[0]

        var entityDaoUpdateWasCalled = false
        var collectionFkPreserved = false

        Mockito
                .`when`(mockEntityDao.fetchOneById(anyInt()))
                .then {
                    // Take entity dao was called to update
                    val entity = ContentEntity()
                    entity.id = it.getArgument(0)
                    entity.collectionFk = 7 // Simulate existing foreign key
                    entity
                }
        Mockito
                .`when`(mockEntityDao.update(helperAny<ContentEntity>()))
                .then {
                    // Take entity dao was called to update
                    entityDaoUpdateWasCalled = true
                    if (it.getArgument<ContentEntity>(0).collectionFk == 7) collectionFkPreserved = true
                    Unit
                }
        Mockito
                .`when`(mockMapper.mapToEntity(helperAny()))
                .then {
                    val chunk = it.getArgument<Maybe<Chunk>>(0).blockingGet()
                    val entity = ContentEntity()
                    entity.id = chunk.id
                    entity.selectedTakeFk = chunk.selectedTake?.id
                    Single.just(entity)
                }

        entityDaoUpdateWasCalled = false
        collectionFkPreserved = false

        dao.update(updatedChunk, null).blockingAwait()
        Assert.assertTrue(entityDaoUpdateWasCalled)
        Assert.assertTrue(collectionFkPreserved)
    }

    @Test
    fun testUpdateTakeNewChunk() {
        val updatedChunk = TestDataStore.chunks[0]
        val newCollection = TestDataStore.collections[0]
        newCollection.id = 32

        var entityDaoUpdateWasCalled = false
        var collectionFkUpdated = false

        Mockito
                .`when`(mockEntityDao.fetchOneById(anyInt()))
                .then {
                    // Take entity dao was called to update
                    val entity = ContentEntity()
                    entity.id = it.getArgument(0)
                    entity.collectionFk = 7 // Simulate existing foreign key
                    entity
                }
        Mockito
                .`when`(mockEntityDao.update(helperAny<ContentEntity>()))
                .then {
                    // Take entity dao was called to update
                    entityDaoUpdateWasCalled = true
                    if (it.getArgument<ContentEntity>(0).collectionFk == newCollection.id) collectionFkUpdated = true
                    Unit
                }
        Mockito
                .`when`(mockMapper.mapToEntity(helperAny()))
                .then {
                    val chunk = it.getArgument<Maybe<Chunk>>(0).blockingGet()
                    val entity = ContentEntity()
                    entity.id = chunk.id
                    entity.selectedTakeFk = chunk.selectedTake?.id
                    Single.just(entity)
                }

        entityDaoUpdateWasCalled = false
        collectionFkUpdated = false

        dao.update(updatedChunk, newCollection).blockingAwait()
        Assert.assertTrue(entityDaoUpdateWasCalled)
        Assert.assertTrue(collectionFkUpdated)
    }

    @Test
    fun testGetSources() {
        // TODO:
        // Set up test derived chunk
        // Set up 2 test source chunks
        // Get get sources for test derived chunk
        // Make sure get both source chunks
    }

    @Test
    fun testAddSource() {
        // TODO:
        // Set up test derived chunk
        // Set up test source chunk
        // Add test source to derived
        // Make sure new link is inserted into derived dao
    }

    @Test
    fun testRemoveSource() {
        // TODO:
        // Set up test derived chunk with existing source link
        // Try to remove the source
        // Make sure row removed from derived table
    }

    @Test
    fun testLinkResource() {
        // TODO:
    }

    @Test
    fun testUnlinkResource() {
        // TODO:
    }

    @Test
    fun testGetResources() {
        // TODO:
    }
}