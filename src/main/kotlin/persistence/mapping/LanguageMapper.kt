package persistence.mapping

import data.Language
import persistence.model.ILanguageEntity
import persistence.model.LanguageEntity

class LanguageMapper: Mapper<ILanguageEntity, Language>{
    override fun mapFromEntity(type: ILanguageEntity): Language {
        return Language(
                type.id,
                type.slug,
                type.name
        )
    }

    override fun mapToEntity(type: Language): ILanguageEntity {
        val languageEntity = LanguageEntity()
        languageEntity.id = type.id
        languageEntity.setName(type.name)
        languageEntity.setSlug(type.slug)
        return languageEntity
    }

}