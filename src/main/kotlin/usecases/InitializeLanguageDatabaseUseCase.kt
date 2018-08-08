package usecases

import api.Door43Client
import api.model.Door43Mapper
import io.reactivex.Completable
import io.reactivex.Observable
import persistence.injection.DaggerPersistenceComponent

class InitializeLanguageDatabaseUseCase {

    private val languageDao = DaggerPersistenceComponent
        .builder()
        .build()
        .injectDatabase()
        .getLanguageDao()

    private val door43Client = Door43Client()

    private val door43Mapper = Door43Mapper

    // inserts languages from Door43 into the database
    fun getAndInsertLanguages(): Completable {
        return Completable.fromObservable(door43Client.getAllLanguages().doOnNext{
            it.map {
                val language = door43Mapper.mapToLanguage(it)
                languageDao.insert(language).onErrorReturn { 0 }.blockingFirst()

            }
        })
    }

}