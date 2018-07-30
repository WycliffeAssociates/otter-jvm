package persistence.mapping

import data.model.Language
import data.model.User
import data.dao.Dao
import data.mapping.Mapper
import data.model.UserPreferences
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import io.reactivex.functions.Function3
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
     * Takes an Observable IUserEntity and maps and returns an Observable User object
     */
    override fun mapFromEntity(type: Observable<IUserEntity>): Observable<User> {
        // queries to find all the source languages

        return type.flatMap {

            val userPreferences: Observable<UserPreferences> = userPreferencesMapper.mapFromEntity(Observable.just(it.userPreferencesEntity))
            val userLanguages: Observable<List<IUserLanguage>> = userLanguageRepo.getByUserId(it.id)

            val sourceLanguages: Observable<List<Language>> = userLanguages.flatMap {
                val listSrcLanguages = it.filter{ it.source }
                        .map{languageRepo.getById(it.languageEntityid)}
                Observable.zip(listSrcLanguages) {
                    it.toList() as List<Language>
                }
            }
            val targetLanguages: Observable<List<Language>> = userLanguages.flatMap {
                val listObsLanguages = it.filter { !it.source }
                        .map { languageRepo.getById(it.languageEntityid)}
                Observable.zip(listObsLanguages) {
                    it.toList() as List<Language>
                }
            }
            val user: Observable<User> = Observable.zip(sourceLanguages, targetLanguages, userPreferences,
                    Function3<List<Language>, List<Language>, UserPreferences, User>{ source, target, pref ->
                        User(it.id, it.audioHash, it.audioPath, source.toMutableList(), target.toMutableList(), pref)})
            user
        }
    }




    /**
     * takes an Observable User object and maps and returns an Observable IUserEntity
     */
    override fun mapToEntity(type: Observable<User>): Observable<IUserEntity> {

        return type.flatMap { user ->
                userPreferencesMapper.mapToEntity(Observable.just(user.userPreferences)).map {
                    val userEntity = UserEntity()
                    userEntity.id = user.id
                    userEntity.setAudioHash(user.audioHash)
                    userEntity.setAudioPath(user.audioPath)
                    userEntity.setUserPreferencesEntity(it)
                userEntity
            }
        }

    }

}