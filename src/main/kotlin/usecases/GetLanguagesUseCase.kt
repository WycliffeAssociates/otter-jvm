package usecases

import api.Door43Client
import api.model.Door43Mapper
import persistence.injection.DaggerDatabaseComponent

class GetLanguagesUseCase {
    val appDatabase = DaggerDatabaseComponent.builder().build().inject()
    val languageDao = appDatabase.getLanguageDao()
    val door43Client = Door43Client()
    val door43Mapper = Door43Mapper

    // inserts languages from Door43 into the database
    fun getAndInsertLanguages() {
        door43Client.getAllLanguages().map {
            it.forEach {
                val language = door43Mapper.mapToLanguage(it)
                languageDao.insert(language)
            }
        }
    }

}