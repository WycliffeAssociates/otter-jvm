package org.wycliffeassociates.otter.jvm.app.ui.viewtakes.viewmodel


import org.wycliffeassociates.otter.jvm.app.ui.viewtakes.model.ViewTakesModel

import tornadofx.*

class ViewTakesViewModel : ViewModel() {
    val model = ViewTakesModel()

    val titleProperty = bind { model.titleProperty }
    val selectedTakeProperty = bind(autocommit = true) { model.selectedTakeProperty }
    val alternateTakes = model.alternateTakes

    fun acceptProposedTake() {
        // Set the comparing take as the selected take

    }

    fun rejectProposedTake() {
        // Reject the proposed take

    }
}