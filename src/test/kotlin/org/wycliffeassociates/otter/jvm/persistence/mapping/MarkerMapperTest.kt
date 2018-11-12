package org.wycliffeassociates.otter.jvm.persistence.mapping

import org.junit.Assert
import org.junit.Test
import org.wycliffeassociates.otter.common.data.model.Marker
import org.wycliffeassociates.otter.jvm.persistence.entities.MarkerEntity
import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.MarkerMapper

class MarkerMapperTest {
    // unit under test
    private val markerMapper = MarkerMapper()

    private val entity = MarkerEntity(
            0,
            0,
            2,
            2000,
            "marker"
    )

    private val marker = Marker(
            2,
            2000,
            "marker",
            0
    )

    @Test
    fun shouldMapEntityToMarker() {
        val result = markerMapper.mapFromEntity(entity)
        Assert.assertEquals(result, marker)
    }

    @Test
    fun shouldMapMarkerToEntity() {
        entity.takeFk = 27
        val result = markerMapper.mapToEntity(marker, 27)
        Assert.assertEquals(entity, result)
    }
}