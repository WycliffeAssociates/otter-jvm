package persistence.data
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface Door43Api {
    @GET("/v3/catalog.json")
    fun getLang(@Query("languages")lang: String): Call<Door43>
}