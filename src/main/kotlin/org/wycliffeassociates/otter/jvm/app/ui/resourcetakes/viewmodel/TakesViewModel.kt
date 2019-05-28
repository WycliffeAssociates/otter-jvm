package org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.viewmodel

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.common.data.workbook.AssociatedAudio
import org.wycliffeassociates.otter.common.data.workbook.Take
import org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.model.TextAudioPair
import tornadofx.*

class TakesViewModel : ViewModel() {
    val titleTextAudioPairProperty = SimpleObjectProperty<TextAudioPair>()
    var titleTextAudioPair by titleTextAudioPairProperty

    val bodyTextAudioPairProperty = SimpleObjectProperty<TextAudioPair?>()
    var bodyTextAudioPair by bodyTextAudioPairProperty

    val activeTextAudioPairProperty = SimpleObjectProperty<TextAudioPair>(titleTextAudioPair)
    var activeTextAudioPair by activeTextAudioPairProperty

    val titleTextProperty = SimpleStringProperty()
    var titleText by titleTextProperty

    val bodyTextProperty = SimpleStringProperty()
    var bodyText by bodyTextProperty

    val titleTakes: ObservableList<Take> = FXCollections.observableArrayList()
    val bodyTakes: ObservableList<Take> = FXCollections.observableArrayList()

    init {
        titleTextAudioPairProperty.onChange {
            titleText = it?.textItem?.text
        }

        bodyTextAudioPairProperty.onChange {
            bodyText = it?.textItem?.text
        }

        loadTakes(titleTextAudioPair, titleTakes)
        bodyTextAudioPair?.let { loadTakes(it, bodyTakes) }
    }

    private fun loadTakes(textAudioPair: TextAudioPair, list: ObservableList<Take>) {
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