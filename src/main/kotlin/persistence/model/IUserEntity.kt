package persistence.model

import data.Language
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

    @get:OneToOne
    @get:ForeignKey(
            update = ReferentialAction.CASCADE,
            delete = ReferentialAction.CASCADE
    )
    val userPreferencesEntity: IUserPreferencesEntity


//    @get:JunctionTable(name = "userTargetLang")
//    @get: ManyToMany(mappedBy = "targetUsers")
//    val targetLanguages: MutableList<ILanguageEntity>
//    @get:JunctionTable(name = "userSrcLang")
//    @get: ManyToMany(mappedBy = "sourceUsers")
//    val sourceLanguages: MutableList<ILanguageEntity>

}