package org.wycliffeassociates.otter.jvm.persistence.mapping

import com.nhaarman.mockitokotlin2.mock
import org.junit.Assert
import org.junit.Test
import org.wycliffeassociates.otter.common.data.model.Marker
import org.wycliffeassociates.otter.common.data.model.Take
import org.wycliffeassociates.otter.jvm.persistence.entities.TakeEntity
import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.TakeMapper
import java.io.File
import java.time.LocalDate

class TakeMapperTest {
    private val mockMarker: Marker = mock()

    // UUT
    private val takeMapper = TakeMapper()

    private val entity = TakeEntity(
            0,
            1,
            "filename.wav",
            "/path/to/filename.wav",
            1,
            "2018-10-05",
            1
    )

    private val take = Take(
            "filename.wav",
            File("/path/to/filename.wav"),
            1,
            LocalDate.of(2018, 10, 5),
            true,
            listOf(mockMarker),
            0
    )

    @Test
    fun shouldMapEntityToTake() {
        entity.played = 1
        take.played = true
        val result = takeMapper.mapFromEntity(entity, listOf(mockMarker))
        Assert.assertEquals(take, result)
    }

    @Test
    fun shouldMapEntityToTakeNotPlayed() {
        entity.played = 0
        take.played = false
        val result = takeMapper.mapFromEntity(entity, listOf(mockMarker))
        Assert.assertEquals(take, result)
    }

    @Test
    fun shouldMapTakeToEntity() {
        take.played = true
        entity.played = 1
        val result = takeMapper.mapToEntity(take, 1)
        Assert.assertEquals(entity, result)
    }

    @Test
    fun shouldMapTakeToEntityNotPlayed() {
        take.played = false
        entity.played = 0
        val result = takeMapper.mapToEntity(take, 1)
        Assert.assertEquals(entity, result)
    }
}