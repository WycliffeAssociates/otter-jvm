package persistence.data

class Door43Retrieval(val api: Api = Api()) {

    fun getLanguages() : List<Language>{
        val langResponse = api.getLanguage("")
        return langResponse.execute().body().languages
    }
}