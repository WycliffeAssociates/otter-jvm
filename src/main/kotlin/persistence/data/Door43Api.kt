package persistence.data
import data.Language
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Door43Api {
    @GET("/exports/langnames.json")
    fun getLanguage(@Query("ln")name: String): Call<List<persistence.data.Language>>
}