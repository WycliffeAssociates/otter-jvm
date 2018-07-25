package persistence.mapping

import data.model.Language
import data.mapping.Mapper
import persistence.model.ILanguageEntity
import persistence.model.LanguageEntity


// Provides a way to go back and forth between language entity (in db) and language object (in our model)
class LanguageMapper: Mapper<ILanguageEntity, Language> {

    // Turns entity into our object
    override fun mapFromEntity(type: ILanguageEntity): Language {
        return Language(
                type.id,
                type.slug,
                type.name,
                type.gateway,
                type.anglicizedName
        )
    }

    // Turns object into entity
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