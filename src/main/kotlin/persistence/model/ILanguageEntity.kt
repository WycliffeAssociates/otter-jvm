package persistence.model

import io.requery.*

@Entity
interface ILanguageEntity: Persistable {

    @get:Key
    @get:Generated
    var id: Int

    @get: Column(unique = true, nullable = false)
    val slug: String
    @get: Column(nullable = false)
    val name: String
}