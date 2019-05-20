package org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.viewmodel

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.common.data.workbook.Take
import org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.model.TextAudioPair
import org.wycliffeassociates.otter.jvm.resourcestestapp.app.ResourceTakesApp
import tornadofx.*

class TakesViewModel : ViewModel() {
    val titleTextAudioPairProperty = SimpleObjectProperty<TextAudioPair>()
    var titleTextAudioPair by titleTextAudioPairProperty

    val bodyTextAudioPairProperty = SimpleObjectProperty<TextAudioPair?>()
    var bodyTextAudioPair by bodyTextAudioPairProperty

    val activeTextAudioPairProperty = SimpleObjectProperty<TextAudioPair>(titleTextAudioPair)
    var activeTextAudioPair by activeTextAudioPairProperty

    val titleTextProperty = SimpleStringProperty("[title]")
    val bodyTextProperty = SimpleStringProperty("[body]")

    val titleTakes: ObservableList<Take> = FXCollections.observableArrayList()
    val bodyTakes: ObservableList<Take> = FXCollections.observableArrayList()

    init {
        titleTextAudioPairProperty.onChange {
            loadTakes(titleTextAudioPair, titleTakes)
            titleTextProperty.set(titleTextAudioPair.textItem.text)
        }

        bodyTextAudioPairProperty.onChange { bodyTextAudioPair ->
            bodyTextAudioPair?.let {
                loadTakes(it, bodyTakes)
                bodyTextProperty.set(it.textItem.text)
            }
        }

//        loadTestTextAudioPairs()
    }

    private fun loadTestTextAudioPairs() {
        titleTextAudioPair = ResourceTakesApp.titleTextAudioPair
        bodyTextAudioPair = ResourceTakesApp.bodyTextAudioPair
    }

    fun loadTakes(textAudioPair: TextAudioPair, list: ObservableList<Take>) {
        list.clear()
        textAudioPair.audio.takes
            .filter { it.deletedTimestamp.value == null }
            .subscribe {
                list.add(it)
                list.removeOnDeleted(it)
            }
    }

    private fun ObservableList<Take>.removeOnDeleted(take: Take) {
        take.deletedTimestamp.subscribe { dateHolder ->
            if (dateHolder.value != null) {
                this.remove(take)
            }
        }
    }

    fun setTitleAsActiveTextAudioPair() {
        activeTextAudioPair = titleTextAudioPair
    }

    fun setBodyAsActiveTextAudioPair() {
        activeTextAudioPair = bodyTextAudioPair
    }
}