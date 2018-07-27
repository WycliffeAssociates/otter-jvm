package persistence.mapping

import data.model.Language
import data.model.UserPreferences
import data.dao.Dao
import data.mapping.Mapper
import persistence.tables.pojos.UserPreferencesEntity
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
<<<<<<< HEAD

class UserPreferencesMapper(private val languageRepo: Dao<Language>):
        Mapper<Observable<UserPreferencesEntity>, Observable<UserPreferences>> {

    override fun mapFromEntity(type: Observable<UserPreferencesEntity>): Observable<UserPreferences> {
        // gets from database and maps preferred source and target language
        return type.flatMap {
            val preferredSourceLanguage = languageRepo.getById(it.sourcelanguagefk)
            val preferredTargetLanguage = languageRepo.getById(it.targetlanguagefk)
            Observable.zip(preferredSourceLanguage, preferredTargetLanguage,
                    BiFunction<Language, Language, UserPreferences> { a, b -> UserPreferences(it.userfk, a, b) })
        }
    }

    override fun mapToEntity(type: Observable<UserPreferences>): Observable<persistence.tables.pojos.UserPreferencesEntity> {
        return type.map {
            UserPreferencesEntity(
                    it.id,
                    it.sourceLanguage.id,
                    it.targetLanguage.id
            )
=======
import io.reactivex.schedulers.Schedulers
import org.reactfx.util.TriFunction
import persistence.model.IUserPreferencesEntity
import persistence.model.UserPreferencesEntity

class UserPreferencesMapper(private val languageRepo: Dao<Language>):
        Mapper<Observable<IUserPreferencesEntity>, Observable<UserPreferences>> {

    override fun mapFromEntity(type: Observable<IUserPreferencesEntity>): Observable<UserPreferences> {
        // gets from database and maps preferred source and target language
        return type.flatMap{
            var preferredSourceLanguage = languageRepo.getById(it.sourceLanguageId)
            var preferredTargetLanguage = languageRepo.getById(it.targetLanguageId)
            Observable.zip(preferredSourceLanguage, preferredTargetLanguage,
                    BiFunction<Language, Language, UserPreferences>
                            {source, target  -> UserPreferences(it.id, source, target)})
        }
    }

    override fun mapToEntity(type: Observable<UserPreferences>): Observable<IUserPreferencesEntity> {
        return type.map {
            val userPreferencesEntity = UserPreferencesEntity()
            userPreferencesEntity.id = it.id
            userPreferencesEntity.setSourceLanguageId(it.sourceLanguage.id)
            userPreferencesEntity.setTargetLanguageId(it.targetLanguage.id)
            userPreferencesEntity
>>>>>>> fixed user preferences mapper
        }
    }
}