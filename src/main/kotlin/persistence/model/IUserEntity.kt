package persistence.model

import io.requery.*

@Entity
interface IUserEntity: Persistable {

    @get:Key
    @get:Generated
    var id: Int

    @get:Column(unique = true, nullable = false)
    var audioHash: String
    @get:Column(unique = true, nullable = false)
    var audioPath: String
}