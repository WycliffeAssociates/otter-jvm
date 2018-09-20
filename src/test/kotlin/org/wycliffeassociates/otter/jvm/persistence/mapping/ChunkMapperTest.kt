package org.wycliffeassociates.otter.jvm.persistence.mapping

import io.reactivex.Observable
import jooq.tables.pojos.ContentEntity
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito
import org.wycliffeassociates.otter.common.data.model.Chunk
import org.wycliffeassociates.otter.jvm.persistence.repo.TakeDao

class ChunkMapperTest {
    val mockTakeDao = Mockito.mock(TakeDao::class.java)

    val TEST_CASES = listOf(
            Pair(
                    ContentEntity(
                            12,
                            null,
                            "labelKey",
                            null,
                            3,
                            1
                    ),
                    Chunk(
                            1,
                            "labelKey",
                            3,
                            3,
                            null,
                            12
                    )
            ),
            Pair(
                    ContentEntity(
                            22,
                            null,
                            "verse41",
                            null,
                            41,
                            40
                    ),
                    Chunk(
                            40,
                            "verse41",
                            41,
                            41,
                            null,
                            22
                    )
            )
    )

    @Test
    fun testIfLanguageEntityCorrectlyMappedToLanguage() {
        for (testCase in TEST_CASES) {
            val input = testCase.first
            val expected = testCase.second

            val result = ChunkMapper(mockTakeDao).mapFromEntity(Observable.just(input)).blockingFirst()
            Assert.assertEquals(expected, result)
        }
    }

    @Test
    fun testIfLanguageCorrectlyMappedToLanguageEntity() {
        for (testCase in TEST_CASES) {
            val input = testCase.second
            val expected = testCase.first

            val result = ChunkMapper(mockTakeDao).mapToEntity(Observable.just(input)).blockingFirst()
            AssertJooq.assertContentEntityEquals(expected, result)
        }
    }
}