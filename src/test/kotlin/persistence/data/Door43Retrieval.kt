package persistence.data

import data.Language

class Door43Retrieval(val api: Api = Api()) {

    fun getLanguages() : List<Lang>{
        val execute = api.getLang("").execute()
        return execute.body()
    }
}