package persistence.data

import data.Language
import io.reactivex.Observable

class LanguageStore {
    companion object {
        //these tests will not work with observables
        /*val languages: List<Lang> = listOf(
                Lang(1, "en", "English", true, "English"),
                Lang(2, "es", "Espanol", true, "Spanish"),
                Lang(3, "fr", "Français", false, "French"),
                Lang(4, "cmn", "官话", true, "Mandarin Chinese"),
                Lang(5, "ar", "العَرَبِيَّة", false, "Arabic")
        )*/

        /*val door43Retrieval = Door43Retrieval()
        val langs: Observable<List<Lang>> = door43Retrieval.getLanguages()
        /*var languages: MutableList<Language> = MutableList(langs.size){ i ->
            Language(langs[i].pk, langs[i].lc, langs[i].ln, langs[i].gw, langs[i].ang) }*/

        var languages: MutableList<Language> = MutableList(){
            i -> (langs.forEach{Language(it.get(i).pk, it.get(i).lc, it.get(i).ln, it.get(i).gw, it.get(i).ang)})}

        fun getLanguageForSlug(slug: String) : Language {
            return languages.filter { it.slug == slug }.first()
        }

        fun getById(id: Int) : Language {
            return languages[id - 1]
        }*/
    }

}