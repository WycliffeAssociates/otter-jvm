/*package persistence.data
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import java.net.URL

interface Door43Api {
    /*@GET("/exports/langnames.json")
    fun getId(@Query("pk")id: Int): Call<Door43>
    fun getSlug(@Query("lc")slug: String): Call<Door43>*/
    @GET("/exports/langnames.json")
    fun getName(@Query("ln")name: String): Call<Lang>
    /*fun getCanBeSource(@Query("gw")canBeSource: Boolean): Call<Door43>
    fun getAnglicizedName(@Query("ang")anglicizedName: String): Call<Door43>*/



}*/