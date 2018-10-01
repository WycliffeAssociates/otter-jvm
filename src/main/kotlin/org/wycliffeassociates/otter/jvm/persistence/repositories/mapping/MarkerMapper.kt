package org.wycliffeassociates.otter.jvm.persistence.repositories.mapping

<<<<<<< HEAD:src/main/kotlin/org/wycliffeassociates/otter/jvm/persistence/repositories/mapping/MarkerMapper.kt
import org.wycliffeassociates.otter.common.data.model.Marker
import org.wycliffeassociates.otter.jvm.persistence.entities.MarkerEntity
=======
import jooq.tables.pojos.MarkerEntity
import org.wycliffeassociates.otter.common.data.model.Marker
import org.wycliffeassociates.otter.common.persistence.mapping.Mapper
>>>>>>> merge mr new persistence:src/main/kotlin/org/wycliffeassociates/otter/jvm/persistence/mapping/MarkerMapper.kt

class MarkerMapper {
    fun mapFromEntity(type: MarkerEntity): Marker {
        return Marker(
                type.number,
                type.position,
                type.label,
                type.id
        )
    }

    fun mapToEntity(type: Marker): MarkerEntity {
        return MarkerEntity(
                type.id,
                null,
                type.number,
                type.position,
                type.label
        )
    }

}