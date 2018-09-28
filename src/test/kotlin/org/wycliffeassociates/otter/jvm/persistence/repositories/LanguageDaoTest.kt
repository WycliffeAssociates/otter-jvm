//package org.wycliffeassociates.otter.jvm.persistence.repositories
//
//import jooq.tables.daos.LanguageEntityDao
//import jooq.tables.pojos.LanguageEntity
//import org.junit.*
//import org.mockito.ArgumentMatchers
//import org.mockito.ArgumentMatchers.*
//import org.mockito.Mockito
//import org.wycliffeassociates.otter.common.data.model.Language
//import org.wycliffeassociates.otter.jvm.persistence.TestDataStore
//import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.LanguageMapper
//
//class LanguageDaoTest {
//    val mockEntityDao: LanguageEntityDao = Mockito.mock(LanguageEntityDao::class.java)
//    val mockMapper: LanguageMapper = Mockito.mock(LanguageMapper::class.java)
//    val dao = LanguageRepository(mockEntityDao, mockMapper)
//    // Required in Kotlin to use Mockito any() argument matcher
//    fun <T> helperAny(): T = ArgumentMatchers.any()
//
//    @Test
//    fun testDelete() {
//        var entityDeleteByIdWasCalled = false
//        var deletedId = 0
//
//        TestDataStore.languages.forEach { language ->
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
//            language.id = TestDataStore.languages.indexOf(language)
//
//            dao.delete(language).blockingAwait()
//
//            Assert.assertTrue(entityDeleteByIdWasCalled)
//            Assert.assertEquals(language.id, deletedId)
//        }
//    }
//
//    @Test
//    fun testGetAll() {
//        Mockito
//                .`when`(mockEntityDao.findAll())
//                .thenReturn(TestDataStore.languages.map {
//                    val entity = LanguageEntity()
//                    entity.id = TestDataStore.languages.indexOf(it)
//                    entity
//                })
//        Mockito
//                .`when`(mockMapper.mapFromEntity(helperAny()))
//                .then {
//                    TestDataStore.languages[it.getArgument<LanguageEntity>(0).id]
//                }
//
//        val retrieved = dao.getAll().blockingGet()
//
//        Assert.assertEquals(TestDataStore.languages.size, retrieved.size)
//        Assert.assertTrue(retrieved.containsAll(TestDataStore.languages))
//    }
//
//    @Test
//    fun testGetById() {
//        Mockito
//                .`when`(mockEntityDao.fetchOneById(anyInt()))
//                .then {
//                    val entity = LanguageEntity()
//                    entity.id = it.getArgument(0)
//                    entity
//                }
//        Mockito
//                .`when`(mockMapper.mapFromEntity(helperAny()))
//                .then {
//                    TestDataStore.languages[it.getArgument<LanguageEntity>(0).id]
//                }
//
//        TestDataStore.languages.forEach {
//            val retrieved = dao.getById(TestDataStore.languages.indexOf(it)).blockingGet()
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
//        TestDataStore.languages.forEach {
//            dao
//                    .getById(TestDataStore.languages.indexOf(it))
//                    .doOnSuccess {
//                        Assert.fail()
//                    }.blockingGet()
//        }
//    }
//
//    @Test
//    fun testGetBySlug() {
//        Mockito
//                .`when`(mockEntityDao.fetchBySlug(anyString()))
//                .then {
//                    val entity = LanguageEntity()
//                    entity.slug = it.getArgument(0)
//                    listOf(entity)
//                }
//        Mockito
//                .`when`(mockMapper.mapFromEntity(helperAny()))
//                .then {
//                    TestDataStore.languages.filter { language ->
//                        it.getArgument<LanguageEntity>(0).slug == language.slug
//                    }.first()
//                }
//
//        TestDataStore.languages.forEach {
//            val retrieved = dao.getBySlug(it.slug).blockingGet()
//            Assert.assertEquals(it, retrieved)
//        }
//    }
//
//    @Test
//    fun testInsertNewLanguage() {
//        Mockito
//                .`when`(mockEntityDao.insert(helperAny<LanguageEntity>()))
//                .then {
//                    val entity: LanguageEntity = it.getArgument(0)
//                    // if input id was 0, it should have been replaced with null
//                    Assert.assertEquals(null, entity.id)
//                }
//        Mockito
//                .`when`(mockEntityDao.fetchBySlug(anyString()))
//                .then {
//                    val entity = LanguageEntity()
//                    entity.slug = it.getArgument(0)
//                    entity.id = TestDataStore.languages
//                            .filter { language ->
//                                entity.slug == language.slug
//                            }
//                            .map {
//                                TestDataStore.languages.indexOf(it)
//                            }
//                            .first()
//                    listOf(entity)
//                }
//        Mockito
//                .`when`(mockMapper.mapFromEntity(helperAny()))
//                .then {
//                    TestDataStore.languages.filter { language ->
//                        it.getArgument<LanguageEntity>(0).slug == language.slug
//                    }.first()
//                }
//        Mockito
//                .`when`(mockMapper.mapToEntity(helperAny()))
//                .then {
//                    val entity = LanguageEntity()
//                    entity.id = it.getArgument<Language>(0).id
//                    entity
//                }
//
//        TestDataStore.languages.forEach {
//            it.id = 0
//            val id = dao.insert(it).blockingGet()
//            Assert.assertEquals(id, TestDataStore.languages.indexOf(it))
//        }
//    }
//
//    @Test
//    fun testUpdateLanguage() {
//        var entityDaoUpdateWasCalled = false
//        Mockito
//                .`when`(mockEntityDao.update(helperAny<LanguageEntity>()))
//                .then {
//                    entityDaoUpdateWasCalled = true
//                    Unit
//                }
//        Mockito
//                .`when`(mockMapper.mapToEntity(helperAny()))
//                .then {
//                    val entity = LanguageEntity()
//                    entity
//                }
//
//        TestDataStore.languages.forEach {
//            entityDaoUpdateWasCalled = false
//            dao.update(it).blockingGet()
//            Assert.assertTrue(entityDaoUpdateWasCalled)
//        }
//    }
//}