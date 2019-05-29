package org.wycliffeassociates.otter.jvm.resourcestestapp.app

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.ReplayRelay
import io.reactivex.Observable
import org.wycliffeassociates.otter.common.data.model.MimeType
import org.wycliffeassociates.otter.common.data.workbook.AssociatedAudio
import org.wycliffeassociates.otter.common.data.workbook.Take
import org.wycliffeassociates.otter.common.data.workbook.TextItem
import org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.model.TextAudioPair
import org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.viewmodel.TakesViewModel
import org.wycliffeassociates.otter.jvm.app.widgets.resourcecard.model.ResourceCardItem
import org.wycliffeassociates.otter.jvm.app.widgets.resourcecard.model.ResourceGroupCardItem
import org.wycliffeassociates.otter.jvm.resourcestestapp.view.ResourcesView
import tornadofx.*
import java.io.File
import java.time.LocalDate

class ResourceTakesApp : App(ResourcesView::class) {

    companion object {
        var titleTextAudioPair: TextAudioPair
        var bodyTextAudioPair: TextAudioPair

        val numTakes = 15

        init {
            titleTextAudioPair = TextAudioPair(
                TextItem("Test resource 1 title", MimeType.MARKDOWN),
                AssociatedAudio(createTakesRelay())
            )

            bodyTextAudioPair = TextAudioPair(
                TextItem("Test resource 1 body", MimeType.MARKDOWN),
                AssociatedAudio(createTakesRelay())
            )
        }

        private fun createTakesRelay(): ReplayRelay<Take> {
            val takesRelay = ReplayRelay.create<Take>()
            val takesList = mutableListOf<Take>()

            for (i in 0..numTakes) {
                takesList.add(createTake(i))
            }
            takesList.forEach {
                takesRelay.accept(it)
            }
            return takesRelay
        }

        private fun createTake(id: Int): Take {
            return Take(
                "take1.wav",
                File("src//main//kotlin//org//wycliffeassociates//otter//jvm//resourcestestapp//takefiles//take1.wav"),
                id,
                MimeType.MARKDOWN,
                LocalDate.parse("2019-04-15"),
                BehaviorRelay.create()
            )
//            return Take(
//                "take2.wav",
//                File("src//main//kotlin//org//wycliffeassociates//otter//jvm//resourcestestapp//takefiles//take2.wav"),
//                1,
//                MimeType.MARKDOWN,
//                LocalDate.parse("2019-04-16"),
//                BehaviorRelay.create()
//            )
        }
    }
}

fun main(args: Array<String>) {
    launch<ResourceTakesApp>(args)
}