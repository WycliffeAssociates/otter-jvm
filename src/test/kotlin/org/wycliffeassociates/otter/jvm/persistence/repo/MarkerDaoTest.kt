package org.wycliffeassociates.otter.jvm.persistence.repo

import jooq.tables.daos.MarkerEntityDao
import jooq.tables.pojos.MarkerEntity
import org.junit.*
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito
import org.wycliffeassociates.otter.common.data.model.Marker
import org.wycliffeassociates.otter.common.data.model.Take
import org.wycliffeassociates.otter.jvm.persistence.TestDataStore
import org.wycliffeassociates.otter.jvm.persistence.mapping.MarkerMapper
import java.io.File
import java.time.ZonedDateTime
import java.util.*

class MarkerDaoTest {
    val mockEntityDao = Mockito.mock(MarkerEntityDao::class.java)
    val mockMapper = Mockito.mock(MarkerMapper::class.java)
    val dao = MarkerDao(mockEntityDao, mockMapper)
    val demoTake = Take(
            "",
            File(""),
            1,
            ZonedDateTime.now(),
            false,
            listOf(),
            0
    )

    // Required in Kotlin to use Mockito any() argument matcher
    fun <T> helperAny(): T = ArgumentMatchers.any()

    @Test
    fun testDelete() {
        var entityDeleteByIdWasCalled = false
        var deletedId = 0

        TestDataStore.markers.forEach { marker ->
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
            marker.id = TestDataStore.markers.indexOf(marker)

            dao.delete(marker).blockingAwait()

            Assert.assertTrue(entityDeleteByIdWasCalled)
            Assert.assertEquals(marker.id, deletedId)
        }
    }

    @Test
    fun testGetAll() {
        Mockito
                .`when`(mockEntityDao.findAll())
                .thenReturn(TestDataStore.markers.map {
                    val entity = MarkerEntity()
                    entity.id = TestDataStore.markers.indexOf(it)
                    entity
                })
        Mockito
                .`when`(mockMapper.mapFromEntity(helperAny()))
                .then {
                    TestDataStore.markers[it.getArgument<MarkerEntity>(0).id]
                }

        val retrieved = dao.getAll().blockingGet()

        Assert.assertEquals(TestDataStore.markers.size, retrieved.size)
        Assert.assertTrue(retrieved.containsAll(TestDataStore.markers))
    }

    @Test
    fun testGetById() {
        Mockito
                .`when`(mockEntityDao.fetchOneById(anyInt()))
                .then {
                    val entity = MarkerEntity()
                    entity.id = it.getArgument(0)
                    entity
                }
        Mockito
                .`when`(mockMapper.mapFromEntity(helperAny()))
                .then {
                    TestDataStore.markers[it.getArgument<MarkerEntity>(0).id]
                }

        TestDataStore.markers.forEach {
            val retrieved = dao.getById(TestDataStore.markers.indexOf(it)).blockingGet()
            Assert.assertEquals(it, retrieved)
        }
    }

    @Test
    fun testGetByNonExistentId() {
        Mockito
                .`when`(mockEntityDao.fetchOneById(anyInt()))
                .thenThrow(RuntimeException::class.java)

        TestDataStore.markers.forEach {
            dao
                    .getById(TestDataStore.markers.indexOf(it))
                    .doOnSuccess {
                        Assert.fail()
                    }.blockingGet()
        }
    }

    @Test
    fun testGetByTake() {
        Mockito
                .`when`(mockEntityDao.fetchByTakeFk(anyInt()))
                .then {
                    val entity = MarkerEntity()
                    // Assume each take has a single marker with same id
                    entity.id = it.getArgument(0)
                    listOf(entity)
                }
        Mockito
                .`when`(mockMapper.mapFromEntity(helperAny()))
                .then {
                    TestDataStore.markers[it.getArgument<MarkerEntity>(0).id]
                }

        TestDataStore.markers.forEach {
            demoTake.id = TestDataStore.markers.indexOf(it)
            val retrieved = dao.getByTake(demoTake).blockingGet()
            Assert.assertEquals(1, retrieved.size)
            Assert.assertTrue(retrieved.contains(it))
        }
    }
    @Test
    fun testInsertNewMarker() {
        var idWasNull = false
        val tmpStore = mutableListOf<MarkerEntity>()

        Mockito
                .`when`(mockEntityDao.insert(helperAny<MarkerEntity>()))
                .thenAnswer {
                    val entity: MarkerEntity = it.getArgument(0)
                    // if input id was 0, it should have been replaced with null
                    if (entity.id == null) idWasNull = true
                    // put in tmpStore
                    entity.id = tmpStore.size + 1
                    tmpStore.add(entity)
                    null
                }
        Mockito
                .`when`(mockEntityDao.findAll())
                .then {
                    tmpStore
                }
        Mockito
                .`when`(mockMapper.mapFromEntity(helperAny()))
                .then {
                    val entity: MarkerEntity = it.getArgument(0)
                    TestDataStore.markers.filter { entity.id == it.id }.first()
                }
        Mockito
                .`when`(mockMapper.mapToEntity(helperAny()))
                .then {
                    val entity = MarkerEntity()
                    entity.id = it.getArgument<Marker>(0).id
                    entity
                }

        TestDataStore.markers.forEach {
            it.id = 0
            idWasNull = false
            tmpStore.clear()
            tmpStore.apply {
                val entity = MarkerEntity()
                entity.id = 1
                add(entity)
            }
            val id = dao.insertForTake(it, demoTake).blockingGet()
            Assert.assertEquals(id, tmpStore.size)
            Assert.assertTrue(idWasNull)
        }
    }

    @Test
    fun testUpdateMarkerNoNewTake() {
        var entityDaoUpdateWasCalled: Boolean
        var takeFkValue: Int
        Mockito
                .`when`(mockEntityDao.fetchOneById(anyInt()))
                .then {
                    val entity = MarkerEntity()
                    entity.id = it.getArgument(0)
                    entity.takeFk = 7 // Simulate existing take fk
                    entity
                }
        Mockito
                .`when`(mockEntityDao.update(helperAny<MarkerEntity>()))
                .then {
                    val entity: MarkerEntity = it.getArgument(0)
                    takeFkValue = entity.takeFk
                    entityDaoUpdateWasCalled = true
                    Unit
                }
        Mockito
                .`when`(mockMapper.mapToEntity(helperAny()))
                .then {
                    val entity = MarkerEntity()
                    entity
                }

        TestDataStore.markers.forEach {
            entityDaoUpdateWasCalled = false
            takeFkValue = -1
            dao.update(it).blockingGet()
            Assert.assertTrue(entityDaoUpdateWasCalled)
            Assert.assertEquals(7, takeFkValue)
        }

    }

    @Test
    fun testUpdateMarkerWithNewTake() {
        var entityDaoUpdateWasCalled: Boolean
        var takeFkValue: Int
        Mockito
                .`when`(mockEntityDao.fetchOneById(anyInt()))
                .then {
                    val entity = MarkerEntity()
                    entity.id = it.getArgument(0)
                    entity.takeFk = 7 // Simulate existing take fk
                    entity
                }
        Mockito
                .`when`(mockEntityDao.update(helperAny<MarkerEntity>()))
                .then {
                    val entity: MarkerEntity = it.getArgument(0)
                    takeFkValue = entity.takeFk
                    entityDaoUpdateWasCalled = true
                    Unit
                }
        Mockito
                .`when`(mockMapper.mapToEntity(helperAny()))
                .then {
                    val entity = MarkerEntity()
                    entity
                }

        TestDataStore.markers.forEach {
            entityDaoUpdateWasCalled = false
            takeFkValue = -1
            demoTake.id = 34
            dao.update(it, demoTake).blockingGet()
            Assert.assertTrue(entityDaoUpdateWasCalled)
            Assert.assertEquals(34, takeFkValue)
        }
    }
}