package org.wycliffeassociates.otter.jvm.persistence.repositories.mapping

import org.wycliffeassociates.otter.common.data.model.Language

import jooq.tables.pojos.LanguageEntity
import org.wycliffeassociates.otter.common.persistence.mapping.Mapper

class LanguageMapper : Mapper<LanguageEntity, Language> {

    override fun mapFromEntity(type: LanguageEntity) =
        Language(
            type.slug,
            type.name,
            type.anglicizedname,
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