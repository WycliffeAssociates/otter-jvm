package org.wycliffeassociates.otter.jvm.persistence.repositories.mapping

import io.reactivex.Maybe
import io.reactivex.Single
import jooq.tables.daos.ContentDerivativeDao
import jooq.tables.daos.ContentEntityDao
import jooq.tables.pojos.ContentEntity
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito
import org.wycliffeassociates.otter.common.data.model.Chunk
import org.wycliffeassociates.otter.jvm.persistence.TestDataStore
import org.wycliffeassociates.otter.jvm.persistence.repositories.TakeRepository

class ChunkMapperTest {
    val mockTakeDao: TakeRepository = Mockito.mock(TakeRepository::class.java)
    val mockDerivedDao: ContentDerivativeDao = Mockito.mock(ContentDerivativeDao::class.java)
    val mockContentEntityDao: ContentEntityDao = Mockito.mock(ContentEntityDao::class.java)

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
                            1,
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

    @Before
    fun setup() {
        // Setup the mock data
        TestDataStore.takes.forEach {
            it.id = TestDataStore.takes.indexOf(it)
        }
        Mockito
                .`when`(mockTakeDao.getById(anyInt()))
                .then {
                    Maybe.just(TestDataStore.takes[it.getArgument(0)])
                }
        // make sure the take with id is used in test pair
        TEST_CASES.forEach {
            if (it.first.selectedTakeFk != null) {
                // Set up the expected selected take
                it.second.selectedTake = TestDataStore.takes[it.first.selectedTakeFk]
            }
        }
    }

    @Test
    fun testIfContentEntityCorrectlyMappedToChunk() {
        for (testCase in TEST_CASES) {
            val input = testCase.first
            val expected = testCase.second

            val result = ChunkMapper(mockTakeDao, mockDerivedDao, mockContentEntityDao).mapFromEntity(Single.just(input)).blockingGet()
            Assert.assertEquals(expected, result)
        }
    }

    @Test
    fun testIfChunkCorrectlyMappedToContentEntity() {
        for (testCase in TEST_CASES) {
            val input = testCase.second
            val expected = testCase.first

            val result = ChunkMapper(mockTakeDao, mockDerivedDao, mockContentEntityDao).mapToEntity(Maybe.just(input)).blockingGet()
            AssertJooq.assertEntityEquals(expected, result)
        }
    }
}