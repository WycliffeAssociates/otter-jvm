//package org.wycliffeassociates.otter.jvm.persistence.repositories
//
//import io.reactivex.Completable
//import io.reactivex.Single
//import jooq.tables.daos.TakeEntityDao
//import jooq.tables.pojos.TakeEntity
//import org.junit.*
//import org.mockito.ArgumentMatchers
//import org.mockito.ArgumentMatchers.*
//import org.mockito.Mockito
//import org.wycliffeassociates.otter.common.data.model.Chunk
//import org.wycliffeassociates.otter.common.data.model.Marker
//import org.wycliffeassociates.otter.common.data.model.Take
//import org.wycliffeassociates.otter.jvm.persistence.TestDataStore
//import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.TakeMapper
//
//class TakeDaoTest {
//    val mockEntityDao: TakeEntityDao = Mockito.mock(TakeEntityDao::class.java)
//    val mockMapper: TakeMapper = Mockito.mock(TakeMapper::class.java)
//    val mockMarkerDao: MarkerDao = Mockito.mock(MarkerDao::class.java)
//    val dao = TakeRepository(mockEntityDao, mockMarkerDao, mockMapper)
//    // Required in Kotlin to use Mockito any() argument matcher
//    fun <T> helperAny(): T = ArgumentMatchers.any()
//
//    @Test
//    fun testDelete() {
//        var entityDeleteByIdWasCalled = false
//        var deletedId = 0
//
//        TestDataStore.takes.forEach { take ->
//            Mockito
//                    .`when`(mockEntityDao.deleteById(anyInt()))
//                    .thenAnswer {
//                        entityDeleteByIdWasCalled = true
//                        deletedId = it.getArgument(0)
//                        null
//                    }
//            // Reset
//            entityDeleteByIdWasCalled = false
//            deletedId = -1
//            take.id = TestDataStore.takes.indexOf(take)
//
//            dao.delete(take).blockingAwait()
//
//            Assert.assertTrue(entityDeleteByIdWasCalled)
//            Assert.assertEquals(take.id, deletedId)
//        }
//    }
//
//    @Test
//    fun testGetAll() {
//        Mockito
//                .`when`(mockEntityDao.findAll())
//                .thenReturn(TestDataStore.takes.map {
//                    val entity = TakeEntity()
//                    entity.id = TestDataStore.takes.indexOf(it)
//                    entity
//                })
//        Mockito
//                .`when`(mockMapper.mapFromEntity(helperAny()))
//                .then {
//                    val entity = it.getArgument<Single<TakeEntity>>(0).blockingGet()
//                    Single.just(TestDataStore.takes[entity.id])
//                }
//
//        val retrieved = dao.getAll().blockingGet()
//
//        Assert.assertEquals(TestDataStore.takes.size, retrieved.size)
//        Assert.assertTrue(retrieved.containsAll(TestDataStore.takes))
//    }
//
//    @Test
//    fun testGetById() {
//        Mockito
//                .`when`(mockEntityDao.fetchOneById(anyInt()))
//                .then {
//                    val entity = TakeEntity()
//                    entity.id = it.getArgument(0)
//                    entity
//                }
//        Mockito
//                .`when`(mockMapper.mapFromEntity(helperAny()))
//                .then {
//                    val entity = it.getArgument<Single<TakeEntity>>(0).blockingGet()
//                    Single.just(TestDataStore.takes[entity.id])
//                }
//
//        TestDataStore.takes.forEach {
//            val retrieved = dao.getById(TestDataStore.takes.indexOf(it)).blockingGet()
//            Assert.assertEquals(it, retrieved)
//        }
//    }
//
//    @Test
//    fun testGetByNonExistentId() {
//        Mockito
//                .`when`(mockEntityDao.fetchOneById(anyInt()))
//                .thenThrow(RuntimeException::class.java)
//
//        TestDataStore.takes.forEach {
//            dao
//                    .getById(TestDataStore.takes.indexOf(it))
//                    .doOnSuccess {
//                        Assert.fail()
//                    }.blockingGet()
//        }
//    }
//
//    @Test
//    fun testGetByChunk() {
//        val testChunk = Chunk(
//                0,
//                "",
//                0,
//                1,
//                null,
//                3
//        )
//        var correctContentFk = false
//        Mockito
//                .`when`(mockEntityDao.fetchByContentFk(anyInt()))
//                .then {
//                    if (it.getArgument<Int>(0) == testChunk.id) correctContentFk = true
//                    TestDataStore.takes.map { take ->
//                        val entity = TakeEntity()
//                        entity.contentFk = it.getArgument(0)
//                        entity.id = TestDataStore.takes.indexOf(take)
//                        entity
//                    }
//                }
//        Mockito
//                .`when`(mockMapper.mapFromEntity(helperAny()))
//                .then {
//                    val entity = it.getArgument<Single<TakeEntity>>(0).blockingGet()
//                    Single.just(TestDataStore.takes[entity.id])
//                }
//
//        correctContentFk = false
//        val retrieved = dao.getByChunk(testChunk).blockingGet()
//        Assert.assertTrue(correctContentFk)
//        Assert.assertEquals(TestDataStore.takes.size, retrieved.size)
//        Assert.assertTrue(retrieved.containsAll(TestDataStore.takes))
//    }
//
//    @Test
//    fun testInsertTakeForChunk() {
//        val theChunk = Chunk(
//                0,
//                "",
//                0,
//                1,
//                null,
//                2
//        )
//        val theTake = TestDataStore.takes[0]
//        val tmpStore = mutableListOf<TakeEntity>()
//
//        var insertedContentFk = 0
//        var idWasNull = false
//        var markerInsertedCorrectly = false
//
//        Mockito
//                .`when`(mockEntityDao.insert(helperAny<TakeEntity>()))
//                .then {
//                    val entity: TakeEntity = it.getArgument(0)
//                    // if input id was 0, it should have been replaced with null
//                    if (entity.id == null) idWasNull = true
//                    insertedContentFk = entity.contentFk
//                    // put in tmpStore
//                    entity.id = tmpStore.size + 1
//                    tmpStore.add(entity)
//                    Unit
//                }
//        Mockito
//                .`when`(mockEntityDao.findAll())
//                .then {
//                    tmpStore
//                }
//        Mockito
//                .`when`(mockMapper.mapFromEntity(helperAny()))
//                .then {
//                    val entity: TakeEntity = it.getArgument(0)
//                    // Just return a value. Not critical for the test
//                    TestDataStore.takes[entity.id]
//                }
//        Mockito
//                .`when`(mockMapper.mapToEntity(helperAny()))
//                .then {
//                    val entity = TakeEntity()
//                    entity.id = it.getArgument<Single<Take>>(0).blockingGet().id
//                    Single.just(entity)
//                }
//        Mockito
//                .`when`(mockMarkerDao.insertForTake(helperAny(), helperAny()))
//                .then {
//                    val marker = it.getArgument<Marker>(0)
//                    val take = it.getArgument<Take>(1)
//                    if (marker == TestDataStore.markers[0] && take == theTake) markerInsertedCorrectly = true
//                    Single.just(0) // Return dummy id
//                }
//
//        theTake.id = 0
//        // Put in some demo markers
//        theTake.markers = listOf(TestDataStore.markers[0])
//        insertedContentFk = 0
//        idWasNull = false
//        markerInsertedCorrectly = false
//        tmpStore.clear()
//        // Add a dummy take to the store
//        tmpStore.apply {
//            val entity = TakeEntity()
//            entity.id = 1
//            add(entity)
//        }
//        val id = dao.insertForChunk(theTake, theChunk).blockingGet()
//        Assert.assertEquals(theChunk.id, insertedContentFk)
//        Assert.assertTrue(idWasNull)
//        Assert.assertTrue(markerInsertedCorrectly)
//        Assert.assertEquals(id, tmpStore.size)
//    }
//
//    @Test
//    fun testUpdateTakeSameChunk() {
//        val theOldMarker = TestDataStore.markers[0]
//        val theNewMarker = TestDataStore.markers[1]
//        val updatedTake = TestDataStore.takes[0]
//        updatedTake.markers = listOf(theNewMarker)
//
//        var entityDaoUpdateWasCalled = false
//        var oldMarkerDeleted = false
//        var newMarkerAdded = false
//        var contentFkPreserved = false
//
//        Mockito
//                .`when`(mockEntityDao.fetchOneById(anyInt()))
//                .then {
//                    // Take entity dao was called to update
//                    val entity = TakeEntity()
//                    entity.id = it.getArgument(0)
//                    entity.contentFk = 7 // Simulate existing foreign key
//                    entity
//                }
//        Mockito
//                .`when`(mockEntityDao.update(helperAny<TakeEntity>()))
//                .then {
//                    // Take entity dao was called to update
//                    entityDaoUpdateWasCalled = true
//                    if (it.getArgument<TakeEntity>(0).contentFk == 7) contentFkPreserved = true
//                    Unit
//                }
//        Mockito
//                .`when`(mockMapper.mapToEntity(helperAny()))
//                .then {
//                    val entity = TakeEntity()
//                    entity.id = it.getArgument<Single<Take>>(0).blockingGet().id
//                    Single.just(entity)
//                }
//
//        Mockito
//                .`when`(mockMarkerDao.delete(theOldMarker))
//                .then {
//                    // The old marker was deleted
//                    oldMarkerDeleted = true
//                    Completable.complete()
//                }
//        Mockito
//                .`when`(mockMarkerDao.insertForTake(theNewMarker, updatedTake))
//                .then {
//                    // The new marker was added
//                    newMarkerAdded = true
//                    Single.just(0)
//                }
//        Mockito
//                .`when`(mockMarkerDao.getByTake(updatedTake))
//                .then {
//                    // Return the list of old markers
//                    Single.just(listOf(theOldMarker))
//                }
//
//        entityDaoUpdateWasCalled = false
//        oldMarkerDeleted = false
//        newMarkerAdded = false
//        contentFkPreserved = false
//
//        dao.update(updatedTake, null).blockingAwait()
//        Assert.assertTrue(entityDaoUpdateWasCalled)
//        Assert.assertTrue(oldMarkerDeleted)
//        Assert.assertTrue(newMarkerAdded)
//        Assert.assertTrue(contentFkPreserved)
//    }
//
//    @Test
//    fun testUpdateTakeNewChunk() {
//        val updatedTake = TestDataStore.takes[0]
//        updatedTake.markers = listOf()
//        val newChunk = TestDataStore.chunks[0]
//        newChunk.id = 12
//
//        var entityDaoUpdateWasCalled = false
//        var contentFkUpdated = false
//
//        Mockito
//                .`when`(mockEntityDao.fetchOneById(anyInt()))
//                .then {
//                    // Take entity dao was called to update
//                    val entity = TakeEntity()
//                    entity.id = it.getArgument(0)
//                    entity.contentFk = 7 // Simulate existing foreign key
//                    entity
//                }
//        Mockito
//                .`when`(mockEntityDao.update(helperAny<TakeEntity>()))
//                .then {
//                    // Take entity dao was called to update
//                    entityDaoUpdateWasCalled = true
//                    if (it.getArgument<TakeEntity>(0).contentFk == newChunk.id) contentFkUpdated = true
//                    Unit
//                }
//        Mockito
//                .`when`(mockMapper.mapToEntity(helperAny()))
//                .then {
//                    val entity = TakeEntity()
//                    entity.id = it.getArgument<Single<Take>>(0).blockingGet().id
//                    Single.just(entity)
//                }
//
//        Mockito
//                .`when`(mockMarkerDao.getByTake(updatedTake))
//                .then {
//                    // Return an empty marker list for this test
//                    Single.just(listOf<Marker>())
//                }
//
//        entityDaoUpdateWasCalled = false
//        contentFkUpdated = false
//
//        dao.update(updatedTake, newChunk).blockingAwait()
//        Assert.assertTrue(entityDaoUpdateWasCalled)
//        Assert.assertTrue(contentFkUpdated)
//    }
//}