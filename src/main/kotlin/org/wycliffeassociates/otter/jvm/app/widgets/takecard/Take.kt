package org.wycliffeassociates.otter.jvm.app.widgets.takecard

import java.io.File
import java.util.*

data class Take(
        var number: Int,
        var date: Date,
        var file: File,
        var played: Boolean = false,
        var id: Int
)