package org.wycliffeassociates.otter.jvm.persistence.mapping

import io.reactivex.Observable
import jooq.tables.pojos.ContentEntity
import jooq.tables.pojos.TakeEntity
import org.junit.Assert
import org.junit.Test
import org.wycliffeassociates.otter.common.data.model.Chunk
import org.wycliffeassociates.otter.common.data.model.Take
import java.io.File
import java.util.*

class TakeMapperTest {
//
//    val TEST_CASES = listOf(
//            Pair(
//                    TakeEntity(
//                            12,
//                            "take01.wav",
//                            "labelKey",
//                            "",
//                            3,
//                            1
//                    ),
//                    Take(
//                            12,
//                            "take01.wav",
//                            File("/path/to/my/take01.wav"),
//                            1,
//                            Calendar.getInstance().apply {
//                                time = Date(837925200000)
//                            },
//                            true
//                    )
//            ),
//            Pair(
//                    ContentEntity(
//                            22,
//                            null,
//                            "verse41",
//                            null,
//                            41,
//                            40
//                    ),
//                    Chunk(
//                            22,
//                            40,
//                            "verse41",
//                            41,
//                            41,
//                            null
//                    )
//            )
//    )
//
//    @Test
//    fun testIfLanguageEntityCorrectlyMappedToLanguage() {
//        for (testCase in TEST_CASES) {
//            val input = testCase.first
//            val expected = testCase.second
//
//            val result = ChunkMapper().mapFromEntity(Observable.just(input)).blockingFirst()
//            Assert.assertEquals(expected, result)
//        }
//    }
//
//    @Test
//    fun testIfLanguageCorrectlyMappedToLanguageEntity() {
//        for (testCase in TEST_CASES) {
//            val input = testCase.second
//            val expected = testCase.first
//
//            val result = ChunkMapper().mapToEntity(Observable.just(input)).blockingFirst()
//            AssertJooq.assertContentEntityEquals(expected, result)
//        }
//    }
}