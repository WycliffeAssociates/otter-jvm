package org.wycliffeassociates.otter.jvm.persistence.mapping

import jooq.tables.pojos.TakeEntity
import org.junit.Assert
import org.junit.Test
import org.wycliffeassociates.otter.common.data.model.Take
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class TakeMapperTest {
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())

    val TEST_CASES = listOf(
            Pair(
                    TakeEntity(
                            12,
                            null,
                            "take01.wav",
                            "/path/to/my/take01.wav",
                            1,
                            dateFormatter.format(Date(837925200000)),
                            1
                    ),
                    Take(
                            "take01.wav",
                            File("/path/to/my/take01.wav"),
                            1,
                            Calendar.getInstance().apply {
                                time = Date(837925200000)
                            },
                            true,
                            12
                    )
            ),
            Pair(
                    TakeEntity(
                            200,
                            null,
                            "take03.wav",
                            "/path/to/my/take03.wav",
                            3,
                            dateFormatter.format(Date(978307200000)),
                            0
                    ),
                    Take(
                            "take03.wav",
                            File("/path/to/my/take03.wav"),
                            3,
                            Calendar.getInstance().apply {
                                time = Date(978307200000)
                            },
                            false,
                            200
                    )
            )
    )

    @Test
    fun testIfTakeEntityCorrectlyMappedToTake() {
        for (testCase in TEST_CASES) {
            val input = testCase.first
            val expected = testCase.second

            val result = TakeMapper().mapFromEntity(input)
            Assert.assertEquals(expected, result)
        }
    }

    @Test
    fun testIfTakeCorrectlyMappedToTakeEntity() {
        for (testCase in TEST_CASES) {
            val input = testCase.second
            val expected = testCase.first

            val result = TakeMapper().mapToEntity(input)
            AssertJooq.assertEntityEquals(expected, result)
        }
    }
}