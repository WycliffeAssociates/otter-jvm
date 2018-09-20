package org.wycliffeassociates.otter.jvm.persistence.mapping

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import jooq.tables.pojos.CollectionEntity
import org.junit.Assert
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito
import org.wycliffeassociates.otter.jvm.persistence.TestDataStore
import org.wycliffeassociates.otter.jvm.persistence.repo.ResourceContainerDao
import org.wycliffeassociates.otter.common.data.model.Collection

class CollectionMapperTest {
    val TEST_CASES = listOf(
            Pair(
                    CollectionEntity(
                            4,
                            null,
                            null,
                            "book",
                            "Romans",
                            "rom",
                            1,
                            0
                    ),
                    Collection(
                            1,
                            "rom",
                            "book",
                            "Romans",
                            TestDataStore.resourceContainers.first(),
                            4
                    )
            ),
            Pair(
                    CollectionEntity(
                            22,
                            null,
                            null,
                            "anthology",
                            "Old Testament",
                            "bible-ot",
                            0,
                            0
                    ),
                    Collection(
                            0,
                            "bible-ot",
                            "anthology",
                            "Old Testament",
                            TestDataStore.resourceContainers.last(),
                            22
                    )
            )
    )

    val mockRcDao: ResourceContainerDao = Mockito.mock(ResourceContainerDao::class.java)

    @Test
    fun testIfCollectionCorrectlyMappedToEntity() {
        for (testCase in TEST_CASES) {
            val input = testCase.second
            val expected = testCase.first
            input.resourceContainer.id = TestDataStore.resourceContainers.indexOf(input.resourceContainer)
            expected.rcFk = input.resourceContainer.id
            val result = CollectionMapper(mockRcDao)
                    .mapToEntity(Maybe.just(input))
                    .blockingGet()
            AssertJooq.assertEntityEquals(expected, result)
        }
    }

    @Test
    fun testIfEntityCorrectlyMappedToCollection() {
        Mockito
                .`when`(mockRcDao.getById(anyInt()))
                .then {
                    val id: Int = it.getArgument(0)
                    val rc = TestDataStore.resourceContainers[id]
                    rc.id = id
                    Maybe.just(rc)
                }

        for (testCase in TEST_CASES) {
            val input = testCase.first
            val expected = testCase.second
            // Set up the ids
            expected.resourceContainer.id = TestDataStore.resourceContainers.indexOf(expected.resourceContainer)
            input.rcFk = expected.resourceContainer.id

            val result = CollectionMapper(mockRcDao)
                    .mapFromEntity(Single.just(input))
                    .blockingGet()
            Assert.assertEquals(expected, result)
        }
    }
}