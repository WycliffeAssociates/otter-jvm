package persistence.mapping

import data.model.Language
import data.model.User
import data.dao.Dao
import data.mapping.Mapper
import data.model.UserPreferences
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import org.reactfx.util.TriFunction
import persistence.model.*
import persistence.repo.UserLanguageRepo
import java.util.*

class UserMapper(
        private val userLanguageRepo: UserLanguageRepo,
        private val languageRepo: Dao<Language>
): Mapper<Observable<IUserEntity>, Observable<User>> {

    private val userPreferencesMapper = UserPreferencesMapper(languageRepo)

    /**
     * Takes a IUserEntity and maps and returns a User object
     */
    override fun mapFromEntity(type: Observable<IUserEntity>): Observable<User> {
        // queries to find all the source languages
        // kept blocking calls here because we need them to be able to return a user rather than an Observable<User>

        type.flatMap{
            
        }

        userLanguages.map({
             sourceLanguages = it.filter{it.source}
                    .map{languageRepo.getById(it.languageEntityid)}
            targetLanguages = it.filter{!it.source}
                    .map{languageRepo.getById(it.languageEntityid)}})


        val userPreferences = userPreferencesMapper.mapFromEntity(type.userPreferencesEntity)

        /* Observable.zip(sourceLanguages, targetLanguages, userPreferences,
                TriFunction<List<Language>, List<Language>, UserPreferences, User>{a, b, c -> User(type.id, type.audioHash, type.audioPath,
                        a.toMutableList(), b.toMutableList(), c)})*/

        /*return User(
                type.id,
                type.audioHash,
                type.audioPath,
                sourceLanguages.toMutableList(),
                targetLanguages.toMutableList(),
                userPreferences
        )*/
    }


    /**
     * takes a User object and maps and returns a IUserEntity
     */
    override fun mapToEntity(type: Observable<User>): Observable<IUserEntity> {
        return type.map{
            val userEntity = UserEntity()
            userEntity.id = it.id
            userEntity.setAudioHash(it.audioHash)
            userEntity.setAudioPath(it.audioPath)
            userEntity.setUserPreferencesEntity(userPreferencesMapper.mapToEntity(it.userPreferences))
            userEntity
        }
    }

}