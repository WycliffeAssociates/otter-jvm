package persistence.model

import io.requery.*

// requery uses this to autogenerate language entities
@Entity
interface ILanguageEntity: Persistable {

    // Autogenerates primary (unique for this db table) key of db for the language entity's row
    @get:Key
    @get:Generated
    var id: Int

    // Puts columns for slug, name, gateway, and anglicized name in language entity's rows
    // Specifications in parentheses
    @get: Column(unique = true, nullable = false)
    val slug: String
    @get: Column(nullable = false)
    val name: String
    @get: Column(nullable = false)
    val gateway: Boolean
    val anglicizedName: String

    // Defines table of relationships
    // Links target users to target languages
    // TODO: many to many code? may need deletion
    @get: ManyToMany(mappedBy = "targetLanguages")
    val targetUsers: MutableList<IUserEntity>
    @get: ManyToMany(mappedBy = "sourceLanguages")
    val sourceUsers: MutableList<IUserEntity>
}