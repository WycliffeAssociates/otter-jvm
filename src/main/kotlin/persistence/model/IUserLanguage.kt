package persistence.model

import io.requery.*

@Entity
interface IUserLanguage: Persistable{

    @get:ForeignKey(
            references = IUserEntity::class,
            delete = ReferentialAction.CASCADE,
            update = ReferentialAction.CASCADE
    )
    @get:Key
    val userEntityid: Int

    @get:ForeignKey(
            references = ILanguageEntity::class,
            delete = ReferentialAction.CASCADE,
            update = ReferentialAction.CASCADE
    )
    @get:Key
    val languageEntityid: Int

    val source: Boolean


}