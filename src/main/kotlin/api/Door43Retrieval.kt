package api

import api.model.Door43Mapper
import io.reactivex.Observable
import persistence.injection.DaggerDatabaseComponent

class Door43Retrieval(val retrofitConfig: RetrofitConfig = RetrofitConfig()) {

    val appDatabase = DaggerDatabaseComponent.builder().build().inject()
    val languageDao = appDatabase.getLanguageDao()

    fun getAllLanguages() : Observable<List<Door43Language>> = retrofitConfig.getLangs()

    fun getAllAndInsert() {
        val door43Mapper = Door43Mapper
        getAllLanguages().map {
            it.forEach {
                val language = door43Mapper.mapToLanguage(it)
                languageDao.insert(language)
            }
        }
    }
}