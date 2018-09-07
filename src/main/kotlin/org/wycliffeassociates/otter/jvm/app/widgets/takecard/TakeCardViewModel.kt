package org.wycliffeassociates.otter.jvm.app.widgets.takecard

import javafx.beans.property.SimpleBooleanProperty
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import org.wycliffeassociates.otter.jvm.app.widgets.audiocard.AudioCardViewModel

class TakeCardViewModel(private val model: TakeCardModel) : AudioCardViewModel(
        model,
        Injector.audioPlayer
) {
    val newBadgeIsVisibleProperty = SimpleBooleanProperty(!model.take.played)

    override fun playPauseButtonPressed() {
        super.playPauseButtonPressed()
        model.take.played = true
        newBadgeIsVisibleProperty.set(!model.take.played)
    }
}