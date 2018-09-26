package org.wycliffeassociates.otter.jvm.persistence.repo

import io.reactivex.Maybe
import io.reactivex.Single
import jooq.tables.daos.DublinCoreEntityDao
import jooq.tables.daos.RcLinkEntityDao
import jooq.tables.pojos.DublinCoreEntity
import jooq.tables.pojos.RcLinkEntity
import org.junit.*
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito
import org.wycliffeassociates.otter.common.data.model.ResourceContainer
import org.wycliffeassociates.otter.jvm.persistence.TestDataStore
import org.wycliffeassociates.otter.jvm.persistence.mapping.ResourceMetadataMapper
import java.io.File
import java.util.*

class ResourceContainerDaoTest {
    val mockEntityDao = Mockito.mock(DublinCoreEntityDao::class.java)
    val mockLinkEntityDao = Mockito.mock(RcLinkEntityDao::class.java)
    val mockMapper = Mockito.mock(ResourceMetadataMapper::class.java)
    val dao = ResourceContainerDao(mockEntityDao, mockLinkEntityDao, mockMapper)

    // Required in Kotlin to use Mockito any() argument matcher
    fun <T> helperAny(): T = ArgumentMatchers.any()

    @Test
    fun testDelete() {
        var entityDeleteByIdWasCalled: Boolean
        var deletedId: Int

        TestDataStore.resourceContainers.forEach { rc ->
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
            rc.id = TestDataStore.resourceContainers.indexOf(rc)

            dao.delete(rc).blockingAwait()

            Assert.assertTrue(entityDeleteByIdWasCalled)
            Assert.assertEquals(rc.id, deletedId)
        }
    }

    @Test
    fun testGetAll() {
        Mockito
                .`when`(mockEntityDao.findAll())
                .thenReturn(TestDataStore.resourceContainers.map {
                    val entity = DublinCoreEntity()
                    entity.id = TestDataStore.resourceContainers.indexOf(it)
                    entity
                })
        Mockito
                .`when`(mockMapper.mapFromEntity(helperAny()))
                .then {
                    Maybe.just(TestDataStore.resourceContainers[
                            it
                                    .getArgument<Single<DublinCoreEntity>>(0)
                                    .blockingGet()
                                    .id
                    ])
                }

        val retrieved = dao.getAll().blockingGet()

        Assert.assertEquals(TestDataStore.resourceContainers.size, retrieved.size)
        Assert.assertTrue(retrieved.containsAll(TestDataStore.resourceContainers))
    }

    @Test
    fun testGetById() {
        Mockito
                .`when`(mockEntityDao.fetchOneById(anyInt()))
                .then {
                    val entity = DublinCoreEntity()
                    entity.id = it.getArgument(0)
                    entity
                }
        Mockito
                .`when`(mockMapper.mapFromEntity(helperAny()))
                .then {
                    Maybe.just(TestDataStore.resourceContainers[
                            it
                                    .getArgument<Single<DublinCoreEntity>>(0)
                                    .blockingGet()
                                    .id
                    ])
                }

        TestDataStore.resourceContainers.forEach {
            val retrieved = dao.getById(TestDataStore.resourceContainers.indexOf(it)).blockingGet()
            Assert.assertEquals(it, retrieved)
        }
    }

    @Test
    fun testGetByNonExistentId() {
        Mockito
                .`when`(mockEntityDao.fetchOneById(anyInt()))
                .thenThrow(RuntimeException::class.java)

        TestDataStore.resourceContainers.forEach {
            dao
                    .getById(TestDataStore.resourceContainers.indexOf(it))
                    .doOnSuccess {
                        Assert.fail()
                    }.blockingGet()
        }
    }

    @Test
    fun testInsertNewResourceContainer() {
        var idWasNull = false
        val tmpStore = mutableListOf<DublinCoreEntity>()

        Mockito
                .`when`(mockEntityDao.insert(helperAny<DublinCoreEntity>()))
                .thenAnswer {
                    val entity: DublinCoreEntity = it.getArgument(0)
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
                    val entity = it.getArgument<Single<DublinCoreEntity>>(0).blockingGet()
                    Maybe.just(TestDataStore.resourceContainers.filter { entity.id == it.id}.first())
                }
        Mockito
                .`when`(mockMapper.mapToEntity(helperAny()))
                .then {
                    val entity = DublinCoreEntity()
                    entity.id = it.getArgument<Maybe<ResourceContainer>>(0).blockingGet().id
                    Single.just(entity)
                }

        TestDataStore.resourceContainers.forEach {
            it.id = 0
            idWasNull = false
            tmpStore.clear()
            tmpStore.apply {
                val entity = DublinCoreEntity()
                entity.id = 1
                add(entity)
            }
            val id = dao.insert(it).blockingGet()
            Assert.assertEquals(id, tmpStore.size)
            Assert.assertTrue(idWasNull)
        }
    }

    @Test
    fun testUpdateResourceContainer() {
        var entityDaoUpdateWasCalled: Boolean
        Mockito
                .`when`(mockEntityDao.update(helperAny<DublinCoreEntity>()))
                .then {
                    entityDaoUpdateWasCalled = true
                    Unit
                }
        Mockito
                .`when`(mockMapper.mapToEntity(helperAny()))
                .then {
                    val entity = DublinCoreEntity()
                    Single.just(entity)
                }

        TestDataStore.resourceContainers.forEach {
            entityDaoUpdateWasCalled = false
            dao.update(it).blockingGet()
            Assert.assertTrue(entityDaoUpdateWasCalled)
        }
    }

    @Test
    fun testAddLink() {
        // Rc1 id is > Rc2 id
        val rc1 = TestDataStore.resourceContainers[0]
        rc1.id = 8
        val rc2 = TestDataStore.resourceContainers[1]
        rc2.id = 4
        var firstId = 0
        var secondId = 0

        Mockito
                .`when`(mockLinkEntityDao.insert(helperAny<RcLinkEntity>()))
                .then {
                    val entity = it.getArgument<RcLinkEntity>(0)
                    firstId = entity.rc1Fk
                    secondId = entity.rc2Fk
                    Unit
                }

        dao.addLink(rc1, rc2).blockingAwait()
        Assert.assertTrue(firstId < secondId)
        Assert.assertEquals(rc2.id, firstId)
        Assert.assertEquals(rc1.id, secondId)
    }

    @Test
    fun testAddLinkCommutative() {
        // Rc1 id is > Rc2 id
        val rc1 = TestDataStore.resourceContainers[0]
        rc1.id = 8
        val rc2 = TestDataStore.resourceContainers[1]
        rc2.id = 4
        var firstId = 0
        var secondId = 0

        Mockito
                .`when`(mockLinkEntityDao.insert(helperAny<RcLinkEntity>()))
                .then {
                    val entity = it.getArgument<RcLinkEntity>(0)
                    firstId = entity.rc1Fk
                    secondId = entity.rc2Fk
                    Unit
                }

        dao.addLink(rc2, rc1).blockingAwait()
        Assert.assertTrue(firstId < secondId)
        Assert.assertEquals(rc2.id, firstId)
        Assert.assertEquals(rc1.id, secondId)
    }

    @Test
    fun testAddDuplicateLink() {
        // Rc2 id is > Rc1 id
        val rc1 = TestDataStore.resourceContainers[0]
        val rc2 = TestDataStore.resourceContainers[1]

        Mockito
                .`when`(mockLinkEntityDao.insert(helperAny<RcLinkEntity>()))
                .thenThrow(RuntimeException::class.java)
        try {
            dao
                    .addLink(rc1, rc2)
                    .blockingAwait()
        } catch (e: RuntimeException) {
            Assert.fail("Didn't handle exception")
        }
    }

    @Test
    fun testGetLinks() {
        val tmpLinks = mutableListOf<RcLinkEntity>()
                .apply {
                    val entity1 = RcLinkEntity(5, 6)
                    val entity2 = RcLinkEntity(2,5)
                    val entity3 = RcLinkEntity(2,4)
                    addAll(listOf(entity1, entity2, entity3))
                }
        Mockito
                .`when`(mockEntityDao.fetchOneById(anyInt()))
                .then {
                    val entity = DublinCoreEntity()
                    entity.id = it.getArgument(0)
                    entity
                }
        Mockito
                .`when`(mockLinkEntityDao.fetchByRc1Fk(anyInt()))
                .then {
                    val fk: Int = it.getArgument(0)
                    tmpLinks.filter { link -> link.rc1Fk == fk }
                }
        Mockito
                .`when`(mockLinkEntityDao.fetchByRc2Fk(anyInt()))
                .then {
                    val fk: Int = it.getArgument(0)
                    tmpLinks.filter { link -> link.rc2Fk == fk }
                }
        Mockito
                .`when`(mockMapper.mapFromEntity(helperAny()))
                .then {
                    val entity = it.getArgument<Single<DublinCoreEntity>>(0).blockingGet()
                    Maybe.just(TestDataStore.resourceContainers.filter { entity.id == it.id }.first())
                }
        val rc = ResourceContainer(
                "",
                "",
                "",
                "",
                "",
                Calendar.getInstance(),
                TestDataStore.languages.first(),
                Calendar.getInstance(),
                "",
                "",
                "",
                "",
                1,
                File(""),
                5
        )
        TestDataStore.resourceContainers[0].id = 6
        TestDataStore.resourceContainers[1].id = 2

        val links = dao.getLinks(rc).blockingGet()
        Assert.assertEquals(2, links.size)
        Assert.assertTrue(links.containsAll(TestDataStore.resourceContainers.subList(0, 1)))
    }

    @Test
    fun testRemoveLink() {
        val rc1 = TestDataStore.resourceContainers[0]
        rc1.id = 5
        val rc2 = TestDataStore.resourceContainers[1]
        rc2.id = 2
        var correctLinkDeleted = false
        val tmpLinks = mutableListOf<RcLinkEntity>()
                .apply {
                    val entity1 = RcLinkEntity(5, 6)
                    val entity2 = RcLinkEntity(2,5)
                    val entity3 = RcLinkEntity(2,4)
                    addAll(listOf(entity1, entity2, entity3))
                }
        Mockito
                .`when`(mockLinkEntityDao.fetchByRc1Fk(anyInt()))
                .then {
                    val fk: Int = it.getArgument(0)
                    tmpLinks.filter { link -> link.rc1Fk == fk }
                }
        Mockito
                .`when`(mockLinkEntityDao.fetchByRc2Fk(anyInt()))
                .then {
                    val fk: Int = it.getArgument(0)
                    tmpLinks.filter { link -> link.rc2Fk == fk }
                }
        Mockito
                .`when`(mockLinkEntityDao.delete(helperAny<RcLinkEntity>()))
                .then {
                    val entity = it.getArgument<RcLinkEntity>(0)
                    if (entity.rc1Fk == 2 && entity.rc2Fk == 5) correctLinkDeleted = true
                    Unit
                }

        correctLinkDeleted = false
        dao.removeLink(rc1, rc2).blockingAwait()
        Assert.assertTrue(correctLinkDeleted)
    }

    @Test
    fun testRemoveLinkCommutative() {
        val rc1 = TestDataStore.resourceContainers[0]
        rc1.id = 5
        val rc2 = TestDataStore.resourceContainers[1]
        rc2.id = 2
        var correctLinkDeleted = false
        val tmpLinks = mutableListOf<RcLinkEntity>()
                .apply {
                    val entity1 = RcLinkEntity(5, 6)
                    val entity2 = RcLinkEntity(2,5)
                    val entity3 = RcLinkEntity(2,4)
                    addAll(listOf(entity1, entity2, entity3))
                }
        Mockito
                .`when`(mockLinkEntityDao.fetchByRc1Fk(anyInt()))
                .then {
                    val fk: Int = it.getArgument(0)
                    tmpLinks.filter { link -> link.rc1Fk == fk }
                }
        Mockito
                .`when`(mockLinkEntityDao.fetchByRc2Fk(anyInt()))
                .then {
                    val fk: Int = it.getArgument(0)
                    tmpLinks.filter { link -> link.rc2Fk == fk }
                }
        Mockito
                .`when`(mockLinkEntityDao.delete(helperAny<RcLinkEntity>()))
                .then {
                    val entity = it.getArgument<RcLinkEntity>(0)
                    if (entity.rc1Fk == 2 && entity.rc2Fk == 5) correctLinkDeleted = true
                    Unit
                }

        correctLinkDeleted = false
        dao.removeLink(rc2, rc1).blockingAwait()
        Assert.assertTrue(correctLinkDeleted)
    }
}