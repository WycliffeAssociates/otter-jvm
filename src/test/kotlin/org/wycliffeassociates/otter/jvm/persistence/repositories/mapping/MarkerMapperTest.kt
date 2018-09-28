package org.wycliffeassociates.otter.jvm.persistence.repositories.mapping

import jooq.tables.pojos.MarkerEntity
import org.junit.Assert
import org.junit.Test
import org.wycliffeassociates.otter.common.data.model.Marker

class MarkerMapperTest {
    val TEST_CASES = listOf(
            Pair(
                    MarkerEntity(
                            12,
                            null,
                            30,
                            198758,
                            "verse30"
                    ),
                    Marker(
                            30,
                            198758,
                            "verse30",
                            12
                    )
            ),
            Pair(
                    MarkerEntity(
                            45,
                            null,
                            10,
                            20485801,
                            "verse10"
                    ),
                    Marker(
                            10,
                            20485801,
                            "verse10",
                            45
                    )
            )
    )

    @Test
    fun testIfMarkerEntityCorrectlyMappedToMarker() {
        for (testCase in TEST_CASES) {
            val input = testCase.first
            val expected = testCase.second

            val result = MarkerMapper().mapFromEntity(input)
            Assert.assertEquals(expected, result)
        }
    }

    @Test
    fun testIfMarkerCorrectlyMappedToMarkerEntity() {
        for (testCase in TEST_CASES) {
            val input = testCase.second
            val expected = testCase.first

            val result = MarkerMapper().mapToEntity(input)
            AssertJooq.assertEntityEquals(expected, result)
        }
    }
}