package persistence.data

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class Api {
    private val door43Api: Door43Api

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.door43.org/")
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

        door43Api = retrofit.create(Door43Api::class.java)
    }

    fun getLanguage(lang: String): Call<Door43> {
        return door43Api.getLang(lang)
    }
}