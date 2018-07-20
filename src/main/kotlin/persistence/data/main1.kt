package persistence.data

import java.net.URL
import java.util.*

fun main(args: Array<String>){
    val door43 = Door43Retrieval()
    for(lang in door43.getLanguages()){
        println(lang.name)
    }
}