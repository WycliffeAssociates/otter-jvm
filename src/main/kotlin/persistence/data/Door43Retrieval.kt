package persistence.data

import data.Language

class Door43Retrieval(val api: Api = Api()) {

    fun getLanguages() : List<persistence.data.Lang>{
        val execute = api.getLang("").execute()
        return execute.body()
    }

    fun getNames() : Array<String>{
        val execute = api.getLang("").execute()
        var i = 0
        var size = execute.body().size
        var names = Array(size) { "n = $it" }
        //var names : Array<String> = arrayOf<String>()
        for(langName in execute.body()){
            names[i] = langName.ln
        }
        return names
    }

    fun getSlugs() : Array<String>{
        val execute = api.getLang("").execute()
        var i = 0
        var size = execute.body().size
        var slugs = Array(size) { "n = $it" }
        //var names : Array<String> = arrayOf<String>()
        for(langName in execute.body()){
            slugs[i] = langName.lc
        }
        return slugs
    }

    fun getAnglicizedNames() : Array<String>{
        val execute = api.getLang("").execute()
        var i = 0
        var size = execute.body().size
        var angs = Array(size) { "n = $it" }
        for(langName in execute.body()){
            angs[i] = langName.ang
        }
        return angs
    }
}