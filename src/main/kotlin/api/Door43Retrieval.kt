package api

import io.reactivex.Observable

class Door43Retrieval(val config: Config = Config()) {

    fun getLanguages() : Observable<List<Door43Language>> = config.getLang("")
}