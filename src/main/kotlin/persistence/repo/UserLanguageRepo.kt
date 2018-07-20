package persistence.repo

import io.reactivex.Completable
import io.reactivex.Observable
import io.requery.Persistable
import io.requery.kotlin.eq
import io.requery.sql.KotlinEntityDataStore
import persistence.model.IUserLanguage

class UserLanguageRepo(private val dataStore: KotlinEntityDataStore<Persistable>) {
    // note that this returns the userEntityId
    fun insert(userLanguageEntity: IUserLanguage): Observable<Int> {
        return Observable.just(dataStore.insert(userLanguageEntity).userEntityid)
    }

    // not inheriting from Dao<IUserLanguage> since a getById function
    // doesn't make sense in this context. reference table row has only composite key
    fun getByUserId(userId: Int): Observable<List<IUserLanguage>> {
        return Observable.just(dataStore.select(IUserLanguage::class).where(IUserLanguage::userEntityid eq userId).get().toList())
    }

    fun getAll(): Observable<List<IUserLanguage>> {
        return Observable.just(dataStore.select(IUserLanguage::class).get().toList())
    }

    // no update since user language table has all columns as part of composite key

    fun delete(userLanguageEntity: IUserLanguage): Completable {
        return Completable.fromAction {
            dataStore.delete(userLanguageEntity)
        }
    }
}