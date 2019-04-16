package org.wycliffeassociates.otter.jvm.resourcestestapp.app

import io.reactivex.Observable
import org.wycliffeassociates.otter.jvm.app.widgets.resourcecard.model.ResourceCardItem
import org.wycliffeassociates.otter.jvm.app.widgets.resourcecard.model.ResourceGroupCardItem
import org.wycliffeassociates.otter.jvm.resourcestestapp.view.ResourcesView
import tornadofx.*

class ResourceTakesApp : App(ResourcesView::class) {

}

fun main(args: Array<String>) {
    launch<ResourceTakesApp>(args)
}
