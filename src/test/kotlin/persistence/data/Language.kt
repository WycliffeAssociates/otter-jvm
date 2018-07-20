package persistence.data

import com.squareup.moshi.Json


class Language(
        val pk: Int,
        val lc: String,
        val ln: String,
        val gw: Boolean,
        val ang: String
)