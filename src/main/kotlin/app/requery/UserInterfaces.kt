package app.requery

import io.requery.*

@Entity
interface User: Persistable{

    @get:Key
    @get:Generated
    var _id: Int

    @get:Column(unique = true, nullable = false)
    var hash: String
    @get:Column(unique = true, nullable = false)
    var recordedNamePath: String
}