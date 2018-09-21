package org.wycliffeassociates.otter.jvm.persistence.mapping

import jooq.tables.pojos.MarkerEntity
import org.wycliffeassociates.otter.common.data.mapping.Mapper
import org.wycliffeassociates.otter.common.data.model.Marker

class MarkerMapper : Mapper<MarkerEntity, Marker> {
    override fun mapFromEntity(type: MarkerEntity): Marker {
        return Marker(
                type.number,
                type.position,
                type.label,
                type.id
        )
    }

    override fun mapToEntity(type: Marker): MarkerEntity {
        return MarkerEntity(
                type.id,
                null,
                type.number,
                type.position,
                type.label
        )
    }

}