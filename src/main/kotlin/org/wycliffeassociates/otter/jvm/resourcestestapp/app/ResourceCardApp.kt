package org.wycliffeassociates.otter.jvm.resourcestestapp.app

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.ReplayRelay
import org.wycliffeassociates.otter.common.data.model.MimeType
import org.wycliffeassociates.otter.common.data.workbook.Resource
import org.wycliffeassociates.otter.common.data.workbook.ResourceGroup
import org.wycliffeassociates.otter.common.data.workbook.AssociatedAudio
import org.wycliffeassociates.otter.common.data.workbook.TextItem
import org.wycliffeassociates.otter.jvm.resourcestestapp.view.ResourcesView
import tornadofx.*

class ResourceCardApp : App(ResourcesView::class) {
    init {
    }

    companion object {
        // Temporary functions to create dummy data
        fun createResourceGroups(): List<ResourceGroup> {
            val groups: MutableList<ResourceGroup> = mutableListOf()
            for (i in 0..175) {
                groups.add(ResourceGroup(createListOfResources(i, (i % 5) + 1), "Verse $i Resources"))
            }
            return groups
        }

        private fun createListOfResources(verseNum: Int, n: Int): List<Resource> {
            val list: MutableList<Resource> = mutableListOf()
            for (i in 1..n) {
                list.add(resource(verseNum, i))
            }
            return list
        }

        private fun resource(verseNum: Int, resourceNum: Int): Resource {
            val titleAudio = AssociatedAudio(ReplayRelay.create(), BehaviorRelay.create())
            val bodyAudio = AssociatedAudio(ReplayRelay.create(), BehaviorRelay.create())
            titleAudio.selected.accept(1)
            bodyAudio.selected.accept(1)
            return Resource(
                0,
                TextItem("Verse $verseNum, Title $resourceNum", MimeType.MARKDOWN),
                null,
                titleAudio,
                bodyAudio
            )
        }
    }
}

fun main(args: Array<String>) {
    launch<ResourceCardApp>(args)
}

