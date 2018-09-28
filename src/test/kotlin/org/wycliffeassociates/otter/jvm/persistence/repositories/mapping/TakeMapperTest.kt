//package org.wycliffeassociates.otter.jvm.persistence.repositories.mapping
//
//import io.reactivex.Single
//import jooq.tables.pojos.TakeEntity
//import org.junit.Assert
//import org.junit.Before
//import org.junit.Test
//import org.mockito.ArgumentMatchers
//import org.mockito.Mockito
//import org.wycliffeassociates.otter.common.data.model.Take
//import org.wycliffeassociates.otter.jvm.persistence.TestDataStore
//import org.wycliffeassociates.otter.jvm.persistence.repositories.MarkerDao
//import java.io.File
//import java.time.ZonedDateTime
//
//class TakeMapperTest {
//    private val mockMarkerDao = Mockito.mock(MarkerDao::class.java)
//
//    val TEST_CASES = listOf(
//            Pair(
//                    TakeEntity(
//                            12,
//                            null,
//                            "take01.wav",
//                            "/path/to/my/take01.wav",
//                            1,
//                            "1450328400000",
//                            1
//                    ),
//                    Take(
//                            "take01.wav",
//                            File("/path/to/my/take01.wav"),
//                            1,
//                            ZonedDateTime.parse("1450328400000"),
//                            true,
//                            TestDataStore.markers,
//                            12
//                    )
//            ),
//            Pair(
//                    TakeEntity(
//                            200,
//                            null,
//                            "take03.wav",
//                            "/path/to/my/take03.wav",
//                            3,
//                            "978307200000",
//                            0
//                    ),
//                    Take(
//                            "take03.wav",
//                            File("/path/to/my/take03.wav"),
//                            3,
//                            ZonedDateTime.parse("1450328400000"),
//                            false,
//                            TestDataStore.markers,
//                            200
//                    )
//            )
//    )
//
//    // Required in Kotlin to use Mockito any() argument matcher
//    fun <T> helperAny(): T = ArgumentMatchers.any()
//
//    @Before
//    fun setup() {
//        Mockito
//                .`when`(mockMarkerDao.getByTake(helperAny()))
//                .thenReturn(Single.just(TestDataStore.markers))
//    }
//
//    @Test
//    fun testIfTakeEntityCorrectlyMappedToTake() {
//        for (testCase in TEST_CASES) {
//            val input = testCase.first
//            val expected = testCase.second
//
//            val result = TakeMapper(mockMarkerDao).mapFromEntity(Single.just(input)).blockingGet()
//            Assert.assertEquals(expected, result)
//        }
//    }
//
//    @Test
//    fun testIfTakeCorrectlyMappedToTakeEntity() {
//        for (testCase in TEST_CASES) {
//            val input = testCase.second
//            val expected = testCase.first
//
//            val result = TakeMapper(mockMarkerDao).mapToEntity(Single.just(input)).blockingGet()
//            AssertJooq.assertEntityEquals(expected, result)
//        }
//    }
//}