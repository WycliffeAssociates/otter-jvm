package org.wycliffeassociates.otter.jvm.app.widgets.takecard

import javafx.beans.binding.BooleanBinding
import org.wycliffeassociates.otter.jvm.app.widgets.audiocard.AudioCardViewModel
import tornadofx.toBinding

class TakeCardViewModel(model: TakeCardModel) : AudioCardViewModel(model) {
    val newBadgeIsVisibleProperty: BooleanBinding = model.takePlayedProperty.toBinding().not()
}