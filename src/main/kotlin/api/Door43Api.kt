package api
import retrofit2.http.GET
import retrofit2.http.Query
import io.reactivex.Observable

interface Door43Api {

    @GET("/exports/langnames.json")
    fun getLanguage(@Query("ln")name: String): Observable<List<Door43Language>>
}