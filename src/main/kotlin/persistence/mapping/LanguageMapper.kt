package persistence.mapping

import data.model.Language
import data.mapping.Mapper
import persistence.model.ILanguageEntity
import persistence.model.LanguageEntity

class LanguageMapper: Mapper<ILanguageEntity, Language> {

    /**
     * Takes an ILanguageEntity and maps and returns a Language object
     */
    override fun mapFromEntity(type: ILanguageEntity): Language =
            Language(
                type.id,
                type.slug,
                type.name,
                type.isGateway,
                type.anglicizedName)

    /**
     * Takes a Language object and maps and returns an ILanguageEntity
     */
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