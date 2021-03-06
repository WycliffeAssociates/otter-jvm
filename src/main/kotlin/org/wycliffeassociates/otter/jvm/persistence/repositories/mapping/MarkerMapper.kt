package org.wycliffeassociates.otter.jvm.persistence.repositories.mapping

import org.wycliffeassociates.otter.common.data.model.Marker
import org.wycliffeassociates.otter.jvm.persistence.entities.MarkerEntity

class MarkerMapper {
    fun mapFromEntity(type: MarkerEntity): Marker {
        return Marker(
            type.number,
            type.position,
            type.label,
            type.id
        )
    }

    fun mapToEntity(type: Marker, takeFk: Int? = null): MarkerEntity {
        return MarkerEntity(
            type.id,
            takeFk,
            type.number,
            type.position,
            type.label
        )
    }
}