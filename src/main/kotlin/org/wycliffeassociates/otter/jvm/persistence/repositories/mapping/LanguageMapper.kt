package org.wycliffeassociates.otter.jvm.persistence.repositories.mapping

import org.wycliffeassociates.otter.common.data.model.Language
<<<<<<< HEAD:src/main/kotlin/org/wycliffeassociates/otter/jvm/persistence/repositories/mapping/LanguageMapper.kt
import org.wycliffeassociates.otter.common.persistence.mapping.Mapper
import org.wycliffeassociates.otter.jvm.persistence.entities.LanguageEntity
=======
import jooq.tables.pojos.LanguageEntity
import org.wycliffeassociates.otter.common.persistence.mapping.Mapper
>>>>>>> merge mr new persistence:src/main/kotlin/org/wycliffeassociates/otter/jvm/persistence/mapping/LanguageMapper.kt

class LanguageMapper : Mapper<LanguageEntity, Language> {

    override fun mapFromEntity(type: LanguageEntity) =
        Language(
            type.slug,
            type.name,
<<<<<<< HEAD:src/main/kotlin/org/wycliffeassociates/otter/jvm/persistence/repositories/mapping/LanguageMapper.kt
            type.anglicizedName,
=======
            type.anglicizedname,
>>>>>>> merge mr new persistence:src/main/kotlin/org/wycliffeassociates/otter/jvm/persistence/mapping/LanguageMapper.kt
            type.direction.toLowerCase(),
            type.isgateway == 1,
            type.id
        )

    override fun mapToEntity(type: Language): LanguageEntity {
        return LanguageEntity(
            type.id,
            type.slug,
            type.name,
            type.anglicizedName,
            type.direction.toLowerCase(),
            if (type.isGateway) 1 else 0
        )
    }

}