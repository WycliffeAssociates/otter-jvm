package org.wycliffeassociates.otter.jvm.persistence.repositories

import com.nhaarman.mockitokotlin2.*
import org.junit.Assert
import org.junit.Test
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.jvm.persistence.database.AppDatabase
import org.wycliffeassociates.otter.jvm.persistence.database.daos.LanguageDao
import org.wycliffeassociates.otter.jvm.persistence.entities.LanguageEntity
import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.LanguageMapper

class LanguageRepositoryTest {
    private val mockEntities = List(2) { i -> mock<LanguageEntity>() }
    private val mockEntity: LanguageEntity = mock()
    private val mockLanguage: Language = mock()
    private val mockDao: LanguageDao = mock {
        on { insert(any(), anyOrNull()) } doReturn 1
        on { insertAll(any(), anyOrNull()) } doReturn List(2) { i -> i }
        on { fetchAll(anyOrNull()) } doReturn mockEntities
        on { fetchGateway(anyOrNull()) } doReturn mockEntities
        on { fetchTargets(anyOrNull()) } doReturn mockEntities
        on { fetchBySlug(any(), anyOrNull()) } doReturn mockEntity

    }
    private val mockDatabase: AppDatabase = mock {
        on { getLanguageDao() } doReturn mockDao
    }
    private val mockMapper: LanguageMapper = mock {
        on { mapFromEntity(any()) } doReturn mockLanguage
        on { mapToEntity(any()) } doReturn mockEntity
    }

    // UUT
    private val repository = LanguageRepository(mockDatabase, mockMapper)

    @Test
    fun shouldMapAndInsertIntoDao() {
        repository.insert(mockLanguage).blockingGet()
        inOrder(mockMapper, mockDao) {
            argumentCaptor<Language> {
                verify(mockMapper).mapToEntity(capture())
                verifyNoMoreInteractions(mockMapper)
                Assert.assertEquals(mockLanguage, firstValue)
            }
            verify(mockDao).insert(any(), anyOrNull())
            verifyNoMoreInteractions(mockDao)
        }
    }

    @Test
    fun shouldMapAndInsertAllIntoDao() {
        val languages = List(2) { mock<Language>() }
        repository.insertAll(languages).blockingGet()
        inOrder(mockMapper, mockDao) {
            argumentCaptor<Language> {
                verify(mockMapper, times(2)).mapToEntity(capture())
                verifyNoMoreInteractions(mockMapper)
                Assert.assertEquals(languages[0], firstValue)
                Assert.assertEquals(languages[1], secondValue)
            }
            verify(mockDao).insertAll(eq(listOf(mockEntity, mockEntity)), anyOrNull())
            verifyNoMoreInteractions(mockDao)
        }
    }

    @Test
    fun shouldGetAllAndMapFromDao() {
        val result = repository.getAll().blockingGet()
        inOrder(mockMapper, mockDao) {
            verify(mockDao).fetchAll(anyOrNull())
            verifyNoMoreInteractions(mockDao)
            argumentCaptor<LanguageEntity> {
                verify(mockMapper, times(mockEntities.size)).mapFromEntity(capture())
                verifyNoMoreInteractions(mockMapper)
                Assert.assertEquals(allValues, mockEntities)
            }
            Assert.assertEquals(mockEntities.size, result.size)
        }
    }

    @Test
    fun shouldGetGatewayFromDao() {
        val result = repository.getGateway().blockingGet()
        inOrder(mockMapper, mockDao) {
            verify(mockDao).fetchGateway(anyOrNull())
            verifyNoMoreInteractions(mockDao)
            argumentCaptor<LanguageEntity> {
                verify(mockMapper, times(mockEntities.size)).mapFromEntity(capture())
                verifyNoMoreInteractions(mockMapper)
                Assert.assertEquals(allValues, mockEntities)
            }
            Assert.assertEquals(mockEntities.size, result.size)
        }
    }

    @Test
    fun shouldGetTargetsFromDao() {
        val result = repository.getTargets().blockingGet()
        inOrder(mockMapper, mockDao) {
            verify(mockDao).fetchTargets(anyOrNull())
            verifyNoMoreInteractions(mockDao)
            argumentCaptor<LanguageEntity> {
                verify(mockMapper, times(mockEntities.size)).mapFromEntity(capture())
                verifyNoMoreInteractions(mockMapper)
                Assert.assertEquals(allValues, mockEntities)
            }
            Assert.assertEquals(mockEntities.size, result.size)
        }
    }

    @Test
    fun shouldGetBySlugFromDao() {
        val slug = "slug"
        repository.getBySlug(slug).blockingGet()
        inOrder(mockMapper, mockDao) {
            verify(mockDao).fetchBySlug(eq(slug), anyOrNull())
            verifyNoMoreInteractions(mockDao)
            argumentCaptor<LanguageEntity> {
                verify(mockMapper).mapFromEntity(capture())
                verifyNoMoreInteractions(mockMapper)
                Assert.assertEquals(mockEntity, firstValue)
            }
        }
    }

    @Test
    fun shouldCallDaoUpdate() {
        val language: Language = mock()
        repository.update(language).blockingAwait()
        inOrder(mockMapper, mockDao) {
            argumentCaptor<Language> {
                verify(mockMapper).mapToEntity(capture())
                verifyNoMoreInteractions(mockMapper)
                Assert.assertEquals(language, firstValue)
            }
            verify(mockDao).update(eq(mockEntity), anyOrNull())
            verifyNoMoreInteractions(mockDao)
        }
    }

    @Test
    fun shouldCallDaoDelete() {
        val language: Language = mock()
        repository.delete(language).blockingAwait()
        inOrder(mockMapper, mockDao) {
            argumentCaptor<Language> {
                verify(mockMapper).mapToEntity(capture())
                verifyNoMoreInteractions(mockMapper)
                Assert.assertEquals(language, firstValue)
            }
            verify(mockDao).delete(eq(mockEntity), anyOrNull())
            verifyNoMoreInteractions(mockDao)
        }
    }

}