package persistence.data
import data.Language
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import io.reactivex.Observable
import java.util.*

interface Door43ApiInterface {

    @GET("/exports/langnames.json")
    fun getLanguage(@Query("ln")name: String): Observable<List<Lang>>
}