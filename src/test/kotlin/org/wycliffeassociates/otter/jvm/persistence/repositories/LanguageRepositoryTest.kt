package org.wycliffeassociates.otter.jvm.persistence.repositories

import com.nhaarman.mockitokotlin2.*
import org.junit.Test
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.jvm.persistence.database.AppDatabase
import org.wycliffeassociates.otter.jvm.persistence.database.daos.LanguageDao
import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.LanguageMapper

class LanguageRepositoryTest {
    private val mockDao: LanguageDao = mock {
        on { insert(any(), anyOrNull()) } doReturn 1
    }
    private val mockDatabase: AppDatabase = mock {
        on { getLanguageDao() } doReturn mockDao
    }
    private val mockMapper: LanguageMapper = mock {
        on { mapFromEntity(any()) } doReturn mock()
        on { mapToEntity(any()) } doReturn mock()
    }

    // UUT
    private val repository = LanguageRepository(mockDatabase, mockMapper)

    @Test
    fun shouldMapAndInsertIntoDao() {
        val language: Language = mock()
        repository.insert(language).blockingGet()
        inOrder(mockMapper, mockDao) {
            verify(mockMapper).mapToEntity(language)
            verifyNoMoreInteractions(mockMapper)
            verify(mockDao).insert(any(), anyOrNull())
            verifyNoMoreInteractions(mockDao)
        }
    }


}