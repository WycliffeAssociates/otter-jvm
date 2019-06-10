package org.wycliffeassociates.otter.jvm.resourcestestapp.app

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.ReplayRelay
import org.wycliffeassociates.otter.common.data.model.ContentType
import org.wycliffeassociates.otter.common.data.model.MimeType
import org.wycliffeassociates.otter.common.data.workbook.*
import org.wycliffeassociates.otter.common.domain.content.Recordable
import org.wycliffeassociates.otter.jvm.resourcestestapp.view.ResourcesView
import tornadofx.*
import java.io.File
import java.time.LocalDate
import java.util.*

class ResourceTakesApp : App(ResourcesView::class) {

    companion object {
        var titleRecordable: Recordable
        var bodyRecordable: Recordable
        val titleAudio = AssociatedAudio(createTakesRelay(15))
        val bodyAudio = AssociatedAudio(createTakesRelay(5))

        init {
            val chunk = createTestChunk()

            val titleComponent = Resource.Component(
                sort = 1,
                textItem = TextItem("Test resource 1 title", MimeType.MARKDOWN),
                audio = titleAudio,
                contentType = ContentType.TITLE
            )

            val bodyComponent = Resource.Component(
                sort = 2,
                textItem = TextItem("Test resource 1 body", MimeType.MARKDOWN),
                audio = bodyAudio,
                contentType = ContentType.BODY
            )

            titleRecordable = Recordable.build(chunk, titleComponent)
            bodyRecordable = Recordable.build(chunk, bodyComponent)
        }

        fun createRandomizedAssociatedAudio(): AssociatedAudio {
            return AssociatedAudio(createTakesRelay(Random().nextInt(10) + 1))
        }

        fun createTestChunk(): Chunk = Chunk(
                sort = 1,
                audio = AssociatedAudio(ReplayRelay.create<Take>()),
                resources = arrayListOf(),
                text = TextItem("Test chunk", MimeType.MARKDOWN),
                start = 1,
                end = 1
            )

        private fun createTakesRelay(numTakes: Int): ReplayRelay<Take> {
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