package org.wycliffeassociates.otter.jvm.resourcecardpage.app

import org.wycliffeassociates.otter.jvm.resourcecardpage.view.ResourcesView
import tornadofx.*

class ResourceCardApp : App(ResourcesView::class) {
    init {
    }
}

fun main(args: Array<String>) {
    launch<ResourceCardApp>(args)
}