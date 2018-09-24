package org.wycliffeassociates.otter.jvm.persistence.repo

import io.reactivex.Maybe
import io.reactivex.Single
import jooq.tables.daos.CollectionEntityDao
import jooq.tables.pojos.CollectionEntity
import org.junit.*
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.*
import org.mockito.Mockito
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.jvm.persistence.TestDataStore
import org.wycliffeassociates.otter.jvm.persistence.mapping.CollectionMapper

class CollectionDaoTest {
    val mockEntityDao = Mockito.mock(CollectionEntityDao::class.java)
    val mockMapper = Mockito.mock(CollectionMapper::class.java)
    val dao = CollectionDao(mockEntityDao, mockMapper)

    // Required in Kotlin to use Mockito any() argument matcher
    fun <T> helperAny(): T = ArgumentMatchers.any()

    @Test
    fun testDelete() {
        var entityDeleteByIdWasCalled: Boolean
        var deletedId: Int

        TestDataStore.collections.forEach { collection ->
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
            collection.id = TestDataStore.collections.indexOf(collection)

            dao.delete(collection).blockingAwait()

            Assert.assertTrue(entityDeleteByIdWasCalled)
            Assert.assertEquals(collection.id, deletedId)
        }
    }

    @Test
    fun testGetAll() {
        Mockito
                .`when`(mockEntityDao.findAll())
                .thenReturn(TestDataStore.collections.map {
                    val entity = CollectionEntity()
                    entity.id = TestDataStore.collections.indexOf(it)
                    entity
                })
        Mockito
                .`when`(mockMapper.mapFromEntity(helperAny()))
                .then {
                    Maybe.just(TestDataStore.collections[
                            it
                                    .getArgument<Single<CollectionEntity>>(0)
                                    .blockingGet()
                                    .id
                    ])
                }

        val retrieved = dao.getAll().blockingGet()

        Assert.assertEquals(TestDataStore.collections.size, retrieved.size)
        Assert.assertTrue(retrieved.containsAll(TestDataStore.collections))
    }

    @Test
    fun testGetById() {
        Mockito
                .`when`(mockEntityDao.fetchOneById(anyInt()))
                .then {
                    val entity = CollectionEntity()
                    entity.id = it.getArgument(0)
                    entity
                }
        Mockito
                .`when`(mockMapper.mapFromEntity(helperAny()))
                .then {
                    Maybe.just(TestDataStore.collections[
                            it
                                    .getArgument<Single<CollectionEntity>>(0)
                                    .blockingGet()
                                    .id
                    ])
                }

        TestDataStore.collections.forEach {
            val retrieved = dao.getById(TestDataStore.collections.indexOf(it)).blockingGet()
            Assert.assertEquals(it, retrieved)
        }
    }

    @Test
    fun testGetByNonExistentId() {
        Mockito
                .`when`(mockEntityDao.fetchOneById(anyInt()))
                .thenThrow(RuntimeException::class.java)

        TestDataStore.collections.forEach {
            dao
                    .getById(TestDataStore.collections.indexOf(it))
                    .doOnSuccess {
                        Assert.fail()
                    }.blockingGet()
        }
    }

    @Test
    fun testInsertNewCollection() {
        var idWasNull = false
        val tmpStore = mutableListOf<CollectionEntity>()

        Mockito
                .`when`(mockEntityDao.insert(helperAny<CollectionEntity>()))
                .thenAnswer {
                    val entity: CollectionEntity = it.getArgument(0)
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
                    val entity = it.getArgument<Single<CollectionEntity>>(0).blockingGet()
                    Maybe.just(TestDataStore.collections.filter { entity.id == it.id}.first())
                }
        Mockito
                .`when`(mockMapper.mapToEntity(helperAny()))
                .then {
                    val entity = CollectionEntity()
                    entity.id = it.getArgument<Maybe<Collection>>(0).blockingGet().id
                    Single.just(entity)
                }

        TestDataStore.collections.forEach {
            it.id = 0
            idWasNull = false
            tmpStore.clear()
            tmpStore.apply {
                val entity = CollectionEntity()
                entity.id = 1
                add(entity)
            }
            val id = dao.insert(it).blockingGet()
            Assert.assertEquals(id, tmpStore.size)
            Assert.assertTrue(idWasNull)
        }
    }

    @Test
    fun testUpdateCollection() {
        var entityDaoUpdateWasCalled: Boolean
        var correctFksPreserved: Boolean
        Mockito
                .`when`(mockEntityDao.fetchOneById(anyInt()))
                .then {
                    val entity = CollectionEntity()
                    entity.id = it.getArgument(0)
                    // Simulate existing foreign keys
                    entity.sourceFk = 3
                    entity.parentFk = 4
                    entity
                }
        Mockito
                .`when`(mockEntityDao.update(helperAny<CollectionEntity>()))
                .then {
                    val entity: CollectionEntity = it.getArgument(0)
                    entityDaoUpdateWasCalled = true
                    if (entity.sourceFk == 3 && entity.parentFk == 4) correctFksPreserved = true
                    Unit
                }
        Mockito
                .`when`(mockMapper.mapToEntity(helperAny()))
                .then {
                    val entity = CollectionEntity()
                    Single.just(entity)
                }

        TestDataStore.collections.forEach {
            entityDaoUpdateWasCalled = false
            correctFksPreserved = false
            dao.update(it).blockingGet()
            Assert.assertTrue(entityDaoUpdateWasCalled)
            Assert.assertTrue(correctFksPreserved)
        }
    }

    @Test
    fun testSetParent() {
        var correctParentSet = false
        Mockito
                .`when`(mockEntityDao.fetchOneById(anyInt()))
                .then {
                    val entity = CollectionEntity()
                    entity
                }
        Mockito
                .`when`(mockEntityDao.update(helperAny<CollectionEntity>()))
                .then {
                    val entity: CollectionEntity = it.getArgument(0)
                    if (entity.parentFk == 2) correctParentSet = true
                    Unit
                }

        val child = TestDataStore.collections[0]
        val parent = TestDataStore.collections[1]
        parent.id = 2
        dao.setParent(child, parent).blockingAwait()
        Assert.assertTrue(correctParentSet)
    }

    @Test
    fun testClearParent() {
        var parentCleared = false
        Mockito
                .`when`(mockEntityDao.fetchOneById(anyInt()))
                .then {
                    val entity = CollectionEntity()
                    entity.parentFk = 1 // existing parent
                    entity
                }
        Mockito
                .`when`(mockEntityDao.update(helperAny<CollectionEntity>()))
                .then {
                    val entity: CollectionEntity = it.getArgument(0)
                    if (entity.parentFk == null) parentCleared = true
                    Unit
                }

        val child = TestDataStore.collections[0]
        dao.setParent(child, null).blockingAwait()
        Assert.assertTrue(parentCleared)
    }

    @Test
    fun testSetSource() {
        var correctSourceSet = false
        Mockito
                .`when`(mockEntityDao.fetchOneById(anyInt()))
                .then {
                    val entity = CollectionEntity()
                    entity
                }
        Mockito
                .`when`(mockEntityDao.update(helperAny<CollectionEntity>()))
                .then {
                    val entity: CollectionEntity = it.getArgument(0)
                    if (entity.sourceFk == 18) correctSourceSet = true
                    Unit
                }

        val derived = TestDataStore.collections[0]
        val source = TestDataStore.collections[1]
        source.id = 18
        dao.setSource(derived, source).blockingAwait()
        Assert.assertTrue(correctSourceSet)
    }

    @Test
    fun testClearSource() {
        var sourceCleared = false
        Mockito
                .`when`(mockEntityDao.fetchOneById(anyInt()))
                .then {
                    val entity = CollectionEntity()
                    entity.sourceFk = 1 // existing parent
                    entity
                }
        Mockito
                .`when`(mockEntityDao.update(helperAny<CollectionEntity>()))
                .then {
                    val entity: CollectionEntity = it.getArgument(0)
                    if (entity.sourceFk == null) sourceCleared = true
                    Unit
                }

        val child = TestDataStore.collections[0]
        dao.setSource(child, null).blockingAwait()
        Assert.assertTrue(sourceCleared)
    }

    @Test
    fun testGetSource() {
        Mockito
                .`when`(mockEntityDao.fetchOneById(anyInt()))
                .then {
                    val entity = CollectionEntity()
                    entity.id = it.getArgument(0)
                    entity.sourceFk = 18
                    entity
                }
        Mockito
                .`when`(mockMapper.mapFromEntity(helperAny()))
                .then {
                    val entity = it.getArgument<Single<CollectionEntity>>(0).blockingGet()
                    Maybe.just(TestDataStore.collections.filter { entity.id == it.id }.first())
                }

        val derived = TestDataStore.collections[0]
        val source = TestDataStore.collections[1]
        source.id = 18
        val retrievedSource = dao.getSource(derived).blockingGet()
        Assert.assertEquals(source, retrievedSource)
    }

    @Test
    fun testGetSourceNoneExists() {
        Mockito
                .`when`(mockEntityDao.fetchOneById(anyInt()))
                .then {
                    val entity = CollectionEntity()
                    entity.id = it.getArgument(0)
                    entity
                }

        val derived = TestDataStore.collections[0]
        dao
                .getSource(derived)
                .doOnSuccess {
                    Assert.fail()
                }.blockingGet()
    }

    @Test
    fun testGetChildren() {
        // Get all Test collections assuming this is parent
        val parent = Collection(
                0,
                "",
                "",
                "",
                TestDataStore.resourceContainers.first(),
                5
        )
        Mockito
                .`when`(mockEntityDao.fetchByParentFk(anyInt()))
                .then {
                    val parentFk: Int = it.getArgument(0)
                    if (parentFk == parent.id) {
                        TestDataStore.collections.map {
                            val entity = CollectionEntity()
                            entity.id = TestDataStore.collections.indexOf(it)
                            entity
                        }
                    } else {
                        listOf()
                    }

                }
        Mockito
                .`when`(mockMapper.mapFromEntity(helperAny()))
                .then {
                    val entity = it.getArgument<Single<CollectionEntity>>(0).blockingGet()
                    Maybe.just(TestDataStore.collections[entity.id])
                }

        val children = dao
                .getChildren(parent)
                .blockingGet()
        Assert.assertEquals(TestDataStore.collections.size, children.size)
        Assert.assertTrue(children.containsAll(TestDataStore.collections))
    }

    @Test
    fun testGetProjects() {
        // Set up an entity as project
        // Set up an entity as not project
        // Return both to findAll
        // Make sure only the project is returned

        val projectEntity = CollectionEntity()
        projectEntity.id = 1
        projectEntity.sourceFk = 2
        projectEntity.parentFk = null

        val sourceEntity = CollectionEntity()
        sourceEntity.id = 0
        sourceEntity.sourceFk = null
        sourceEntity.parentFk = null

        Mockito
                .`when`(mockEntityDao.findAll())
                .then {
                    listOf(projectEntity, sourceEntity)
                }
        Mockito
                .`when`(mockMapper.mapFromEntity(helperAny()))
                .then {
                    val entity = it.getArgument<Single<CollectionEntity>>(0).blockingGet()
                    Maybe.just(TestDataStore.collections[entity.id])
                }

        val projects = dao
                .getProjects()
                .blockingGet()
        Assert.assertEquals(1, projects.size)
        Assert.assertTrue(projects.contains(TestDataStore.collections[projectEntity.id]))
    }

    @Test
    fun testGetSources() {
        // Set up an entity as a project
        // Set up an entity as a source
        // Set up find all to return both
        // Make sure only the source is returned

        val projectEntity = CollectionEntity()
        projectEntity.id = 1
        projectEntity.sourceFk = 2
        projectEntity.parentFk = null

        val sourceEntity = CollectionEntity()
        sourceEntity.id = 0
        sourceEntity.sourceFk = null
        sourceEntity.parentFk = null

        Mockito
                .`when`(mockEntityDao.findAll())
                .then {
                    listOf(projectEntity, sourceEntity)
                }
        Mockito
                .`when`(mockMapper.mapFromEntity(helperAny()))
                .then {
                    val entity = it.getArgument<Single<CollectionEntity>>(0).blockingGet()
                    Maybe.just(TestDataStore.collections[entity.id])
                }

        val sources = dao
                .getSources()
                .blockingGet()
        Assert.assertEquals(1, sources.size)
        Assert.assertTrue(sources.contains(TestDataStore.collections[sourceEntity.id]))
    }
}