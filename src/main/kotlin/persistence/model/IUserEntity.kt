package persistence.model

import io.requery.*

@Entity
interface IUserEntity: Persistable {

    @get:Key
    @get:Generated
    var id: Int

    @get:Column(unique = true, nullable = false)
    val audioHash: String
    @get:Column(unique = true, nullable = false)
    val audioPath: String

    @get:ForeignKey(
            references = IUserPreferencesEntity::class,
            update = ReferentialAction.CASCADE,
            delete = ReferentialAction.CASCADE
    )
    @get:OneToOne
    val userPreferencesEntity: IUserPreferencesEntity

}