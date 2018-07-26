package api

import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*

// Gets objects from JSON (all of which are languages)
// As of now, the JSON is  a list of languages and we turn them into objects and keep them in a door43api object
class Config {
    // Will store all our language objects and provide getters
    private val door43Api: Door43Api

    // Gets location of resource folder
    private val locale = Locale.getDefault()
    private val labels = ResourceBundle.getBundle("Resources", locale)

    init {
        // Configures retrofit
        val retrofit = Retrofit.Builder()
                // Gets base url for where to retrieve json
                .baseUrl(labels.getString("base_json"))
                // Turns json into objects using moshi
                .addConverterFactory(MoshiConverterFactory.create())
                // We need rx because the objects it turns into will be observables
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                // Tells computer to actually do the above
                .build()

        // Data object that contains the objects just built
        door43Api = retrofit.create(Door43Api::class.java)

    }

    // Gets a language from the objects just built
    fun getLang(lang: String): Observable<List<Door43Language>> {
        return door43Api.getLanguage(lang)
    }
}
