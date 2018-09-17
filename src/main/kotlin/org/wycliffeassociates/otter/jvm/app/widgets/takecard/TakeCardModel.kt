package org.wycliffeassociates.otter.jvm.app.widgets.takecard

import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import org.wycliffeassociates.otter.jvm.app.widgets.audiocard.AudioCardModel
import tornadofx.getProperty
import tornadofx.property
import java.io.File
import java.util.Date

class TakeCardModel(
        take: Take
) : AudioCardModel(
        String.format("%02d", take.number),
        String.format("%tD", take.date),
        take.file,
        Injector.audioPlayer
) {

    var takeNumber: Int by property(take.number)
    val takeNumberProperty = getProperty(TakeCardModel::takeNumber)

    var takeDate: Date by property(take.date)
    val takeDateProperty = getProperty(TakeCardModel::takeDate)

    var takeFile: File by property(take.file)
    val takeFileProperty = getProperty(TakeCardModel::takeFile)

    var takePlayed: Boolean by property(take.played)
    val takePlayedProperty = getProperty(TakeCardModel::takePlayed)

    init {
        // listen to update super's model
        titleProperty.bind(takeNumberProperty.asString("%02d"))
        subtitleProperty.bind(takeDateProperty.asString("%tD"))
        audioFileProperty.bind(takeFileProperty)
    }

    override fun play() {
        super.play()
        takePlayed = true
    }

}