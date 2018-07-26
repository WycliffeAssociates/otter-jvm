package persistence.model

import io.requery.*

@Entity
interface IUserLanguage: Persistable {

    @get:ForeignKey(
            references = IUserEntity::class
    )
    @get:Key
    val userEntityid: Int

    @get:ForeignKey(
            references = ILanguageEntity::class
    )
    @get:Key
    val languageEntityid: Int

    @get:Key
    val source: Boolean
}