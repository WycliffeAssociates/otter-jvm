package api

import io.reactivex.Observable

// Lets us get the languages we made in Config so we can put them into the db
class Door43Retrieval(val config: Config = Config()) {

    // Gets languages as observable list from config
    fun getLanguages() : Observable<List<Door43Language>>{
        return config.getLang("")
    }
}