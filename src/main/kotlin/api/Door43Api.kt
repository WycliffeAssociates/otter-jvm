package api

import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*

class Door43Api {
    private val door43ApiInterface: Door43ApiInterface

    val locale = Locale.getDefault()
    val labels = ResourceBundle.getBundle("Resources", locale)

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl(labels.getString("base_json"))
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        door43ApiInterface = retrofit.create(Door43ApiInterface::class.java)

    }

    fun getLang(lang: String): Observable<List<Lang>> {
        return door43ApiInterface.getLanguage(lang)
    }
}
