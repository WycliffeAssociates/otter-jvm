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
    @get: Column(nullable = false)
    val gateway: Boolean
    val anglicizedName: String

    @get: ManyToMany(mappedBy = "targetLanguages")
    val targetUsers: MutableList<IUserEntity>
    @get: ManyToMany(mappedBy = "sourceLanguages")
    val sourceUsers: MutableList<IUserEntity>
}