package org.wycliffeassociates.otter.jvm.resourcestestapp.app

import org.wycliffeassociates.otter.jvm.resourcestestapp.view.ResourcesView
import tornadofx.*

class ResourceCardApp : App(ResourcesView::class)

fun main(args: Array<String>) {
    launch<ResourceCardApp>(args)
}