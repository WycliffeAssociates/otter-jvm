package persistence.model

import io.requery.*

@Entity
interface IUserPreferencesEntity: Persistable {

    @get:Key
    @get:Generated
    var id: Int

    @get:ForeignKey(
            references = ILanguageEntity::class
    )
    @get:Column(nullable = false)
    val sourceLanguageId: Int

    @get:ForeignKey(
            references = ILanguageEntity::class
    )
    @get:Column(nullable = false)
    val targetLanguageId: Int
}