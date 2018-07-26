package api
import retrofit2.http.GET
import retrofit2.http.Query
import io.reactivex.Observable

interface Door43Api {

    // Extension added to the base URL of the json
    @GET("/exports/langnames.json")
    // Finds all places ln occurs in the JSON
    // (ln is the language's name, used to separate the languages because each language has one)
    // Makes a new POJO every time it sees ln
    fun getLanguage(@Query("ln")name: String): Observable<List<Door43Language>>
}