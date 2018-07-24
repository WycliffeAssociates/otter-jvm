package api

import io.reactivex.Observable

class Door43Retrieval(val door43api: Door43Api = Door43Api()) {

    fun getLanguages() : Observable<List<Lang>>{
        return door43api.getLang("")
    }
}