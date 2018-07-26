package api

import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*

class Config {
    private val door43Api: Door43Api
    private val locale = Locale.getDefault()
    private val labels = ResourceBundle.getBundle("Resources", locale)

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl(labels.getString("base_json"))
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        door43Api = retrofit.create(Door43Api::class.java)

    }

    fun getLang(lang: String) = door43Api.getLanguage(lang)
}
