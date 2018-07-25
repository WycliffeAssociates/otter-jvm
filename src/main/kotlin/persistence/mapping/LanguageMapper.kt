package persistence.mapping

import data.model.Language
import data.mapping.Mapper
import persistence.model.ILanguageEntity
import persistence.model.LanguageEntity

class LanguageMapper: Mapper<ILanguageEntity, Language> {
    override fun mapFromEntity(type: ILanguageEntity): Language {
        return Language(
                type.id,
                type.slug,
                type.name,
                type.gateway,
                type.anglicizedName
        )
    }

    override fun mapToEntity(type: Language): ILanguageEntity {
        val languageEntity = LanguageEntity()
        languageEntity.id = type.id
        languageEntity.setName(type.name)
        languageEntity.setSlug(type.slug)
        languageEntity.setGateway(type.isGateway)
        languageEntity.setAnglicizedName(type.anglicizedName)
        return languageEntity
    }

}