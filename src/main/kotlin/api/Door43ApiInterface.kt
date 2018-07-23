package api
import retrofit2.http.GET
import retrofit2.http.Query
import io.reactivex.Observable

interface Door43ApiInterface {

    @GET("/exports/langnames.json")
    fun getLanguage(@Query("ln")name: String): Observable<List<Lang>>
}