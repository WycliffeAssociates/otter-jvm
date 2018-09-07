package org.wycliffeassociates.otter.jvm.app.widgets.takecard

import org.wycliffeassociates.otter.jvm.app.widgets.audiocard.AudioCardModel
import java.text.SimpleDateFormat

data class TakeCardModel(
        val take: Take
) : AudioCardModel(
        "${take.number}",
        SimpleDateFormat("MM/dd/yyyy").format(take.date),
        take.file
)