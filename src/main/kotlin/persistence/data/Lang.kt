package persistence.data

import com.squareup.moshi.Json


class Lang(
        var pk: Int,
        var lc: String,
        var ln: String,
        var gw: Boolean,
        var ang: String
)