package persistence.repo

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import io.requery.Persistable
import io.requery.kotlin.eq
import io.requery.reactivex.KotlinReactiveEntityStore
import io.requery.sql.KotlinEntityDataStore
import persistence.model.IUserLanguage

class UserLanguageRepo(private val dataStore: KotlinEntityDataStore<Persistable>) {

    // note that this returns the userEntityId
    fun insert(userLanguageEntity: IUserLanguage): Observable<Int> {
        return Observable.create<Int> {
            it.onNext(dataStore.insert(userLanguageEntity).userEntityid)
        }.subscribeOn(Schedulers.io())
    }

    // not inheriting from Dao<IUserLanguage> since a getById function
    // doesn't make sense in this context. reference table row has only composite key
    fun getByUserId(userId: Int): Observable<List<IUserLanguage>> {
        return Observable.create<List<IUserLanguage>> {
            it.onNext(
                    dataStore
                            .select(IUserLanguage::class)
                            .where(IUserLanguage::userEntityid eq userId)
                            .get()
                            .toList()
            )
        }.subscribeOn(Schedulers.io())
    }

    fun getAll(): Observable<List<IUserLanguage>> {
        return Observable.create<List<IUserLanguage>> {
           it.onNext(
                   dataStore
                           .select(IUserLanguage::class)
                           .get()
                           .toList()
           )
        }.subscribeOn(Schedulers.io())
    }

    // no update since user language table has all columns as part of composite key
    fun delete(userLanguageEntity: IUserLanguage): Completable {
        return Completable.fromAction {
            dataStore.delete(userLanguageEntity)
        }.subscribeOn(Schedulers.io())
    }
}