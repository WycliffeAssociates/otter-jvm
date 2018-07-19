package persistence.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import data.Language
import jdk.nashorn.internal.ir.RuntimeNode
import java.net.URL
import java.util.*

class Door43 {
    fun parseJson(json: String) : persistence.data.Language{
        val moshi = Moshi.Builder().build()
        var adapter = moshi.adapter(persistence.data.Language::class.java)
        val languages = adapter.fromJson(json)
        return languages
    }
}