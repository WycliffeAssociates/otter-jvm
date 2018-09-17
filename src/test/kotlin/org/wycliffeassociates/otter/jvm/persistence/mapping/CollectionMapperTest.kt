package org.wycliffeassociates.otter.jvm.persistence.mapping

import io.reactivex.Observable
import jooq.tables.pojos.CollectionEntity
import org.junit.Assert
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito
import org.wycliffeassociates.otter.common.data.dao.Dao
import org.wycliffeassociates.otter.jvm.persistence.TestDataStore
import org.wycliffeassociates.otter.jvm.persistence.repo.DefaultResourceContainerDao
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.ResourceContainer

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
                            4,
                            1,
                            "rom",
                            "book",
                            "Romans",
                            TestDataStore.resourceContainers.first()
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
                            22,
                            0,
                            "bible-ot",
                            "anthology",
                            "Old Testament",
                            TestDataStore.resourceContainers.last()
                    )
            )
    )

    val mockRcDao: Dao<ResourceContainer> = Mockito.mock(DefaultResourceContainerDao::class.java)

    @Test
    fun testIfCollectionCorrectlyMappedToEntity() {
        for (testCase in TEST_CASES) {
            val input = testCase.second
            val expected = testCase.first
            input.resourceContainer.id = TestDataStore.resourceContainers.indexOf(input.resourceContainer)
            expected.rcFk = input.resourceContainer.id
            val result = CollectionMapper(mockRcDao)
                    .mapToEntity(Observable.just(input))
                    .blockingFirst()
            AssertJooq.assertCollectionEntityEquals(expected, result)
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
                    Observable.just(rc)
                }

        for (testCase in TEST_CASES) {
            val input = testCase.first
            val expected = testCase.second
            // Set up the ids
            expected.resourceContainer.id = TestDataStore.resourceContainers.indexOf(expected.resourceContainer)
            input.rcFk = expected.resourceContainer.id

            val result = CollectionMapper(mockRcDao)
                    .mapFromEntity(Observable.just(input))
                    .blockingFirst()
            Assert.assertEquals(expected, result)
        }
    }
}