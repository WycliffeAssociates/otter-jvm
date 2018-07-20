package persistence.data

import data.Language
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class Api {
    private val door43Api: Door43Api

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://td.unfoldingword.org/")
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

        door43Api = retrofit.create(Door43Api::class.java)

    }

    fun getLang(lang: String): Call<List<persistence.data.Lang>> {
        return door43Api.getLanguage(lang)
    }
}