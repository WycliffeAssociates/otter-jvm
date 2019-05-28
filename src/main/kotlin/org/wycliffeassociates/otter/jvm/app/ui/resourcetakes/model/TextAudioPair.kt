package org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.model

import org.wycliffeassociates.otter.common.data.workbook.AssociatedAudio
import org.wycliffeassociates.otter.common.data.workbook.TextItem

data class TextAudioPair(
    val textItem: TextItem,
    val audio: AssociatedAudio
)