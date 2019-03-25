package org.wycliffeassociates.otter.jvm.resourcecardpage.app

import javafx.stage.Stage
import javafx.stage.StageStyle
import org.wycliffeassociates.otter.jvm.app.theme.AppStyles
import org.wycliffeassociates.otter.jvm.app.ui.splash.view.SplashScreen
import org.wycliffeassociates.otter.jvm.resourcecardpage.view.ResourcePageView
import tornadofx.*

class ResourceCardApp : App(ResourcePageView::class) {
    init {
    }
}

fun main(args: Array<String>) {
    launch<ResourceCardApp>(args)
}
