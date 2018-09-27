package org.wycliffeassociates.otter.jvm.persistence.mapping

import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.common.data.mapping.Mapper
import jooq.tables.pojos.LanguageEntity

class LanguageMapper : Mapper<LanguageEntity, Language> {

    override fun mapFromEntity(type: LanguageEntity) =
        Language(
            type.slug,
            type.name,
            type.anglicized,
            type.direction.toLowerCase(),
            type.gateway == 1,
            type.id
        )

    override fun mapToEntity(type: Language): LanguageEntity {
        return LanguageEntity(
            type.id,
            type.slug,
            type.name,
            if (type.isGateway) 1 else 0,
            type.anglicizedName,
            type.direction.toLowerCase()
        )
    }

}