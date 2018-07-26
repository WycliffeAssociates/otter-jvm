package api

import io.reactivex.Observable

class Door43Retrieval(val retrofitConfig: RetrofitConfig = RetrofitConfig()) {

    fun getLanguages() : Observable<List<Door43Language>> = retrofitConfig.getLang("")
}