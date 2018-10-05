package org.wycliffeassociates.otter.jvm.app.ui.viewtakes.model

import com.github.thomasnield.rxkotlinfx.observeOnFx
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.common.data.model.Chunk
import org.wycliffeassociates.otter.common.data.model.Take
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import org.wycliffeassociates.otter.jvm.app.ui.projectpage.viewmodel.ProjectPageViewModel
import tornadofx.*

class ViewTakesModel {

    val chunkProperty = find(ProjectPageViewModel::class).activeChunkProperty

    val selectedTakeProperty = SimpleObjectProperty<Take>()

    val alternateTakes: ObservableList<Take> = FXCollections.observableList(mutableListOf())

    var title: String by property("View Takes")
    var titleProperty = getProperty(ViewTakesModel::title)

    init {
        // TODO: Add to use case
        chunkProperty.onChange {
            alternateTakes.clear()
            if (it != null) {
                populateTakes(it)
            }
        }
        chunkProperty.value?.let { populateTakes(it) }
    }

    private fun populateTakes(chunk: Chunk) {
        Injector
                .takeRepository
                .getByChunk(chunk)
                .observeOnFx()
                .subscribe { retrievedTakes ->
                    alternateTakes.clear()
                    alternateTakes.addAll(retrievedTakes)
                    selectedTakeProperty.value = null
                }
    }

    fun rejectProposedTake() {

    }

    fun acceptProposedTake() {

    }
}

