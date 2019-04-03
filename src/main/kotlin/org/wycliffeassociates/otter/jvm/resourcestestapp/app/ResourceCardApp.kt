package org.wycliffeassociates.otter.jvm.resourcestestapp.app

import io.reactivex.Observable
import javafx.beans.value.ObservableValue
import org.wycliffeassociates.otter.jvm.app.widgets.resourcecard.model.ResourceCardItem
import org.wycliffeassociates.otter.jvm.app.widgets.resourcecard.model.ResourceGroupCardItem
import org.wycliffeassociates.otter.jvm.resourcestestapp.view.ResourcesView
import tornadofx.*

class ResourceCardApp : App(ResourcesView::class) {

    companion object {
        // Temporary functions to create dummy data
//        fun createResourceGroups(): List<ResourceGroupCardItem> {
//            val groups: MutableList<ResourceGroupCardItem> = mutableListOf()
//            for (i in 0..175) {
//                groups.add(ResourceGroupCardItem("Verse $i Resources", createListOfResources(i, (i % 5) + 1)))
//            }
//            return groups
//        }
//
//        private fun createListOfResources(verseNum: Int, n: Int): Observable<ResourceCardItem> {
//            val obs = Observable.fromPublisher<ResourceCardItem> { pub ->
//                for (i in 1..n) {
//                    pub.onNext(resourceCardItem(verseNum, i))
//                }
//            }
//            return obs
//        }
//
//        private fun resourceCardItem(verseNum: Int, resourceNum: Int): ResourceCardItem {
//            return ResourceCardItem("Verse $verseNum, Title $resourceNum")
//        }
    }
}

fun main(args: Array<String>) {
    launch<ResourceCardApp>(args)
}
