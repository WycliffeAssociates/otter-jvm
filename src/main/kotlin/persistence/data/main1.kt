package persistence.data

import java.net.URL
import java.util.*

fun main(args: Array<String>){
    var response = ""
    val connection = URL("https://td.unfoldingword.org/exports/langnames.json").openConnection()
    Scanner(connection.getInputStream()).use({ scanner ->
        response = scanner.useDelimiter("\\A").next()
    })

    response = response.substring(1, response.length-1)

    val door43 = Door43()
    println(door43.parseJson(response).ang)
    println(door43.parseJson(response).gw)
    println(door43.parseJson(response).lc)
    println(door43.parseJson(response).ln)
    println(door43.parseJson(response).pk)
}