package org.wycliffeassociates.otter.jvm.app

import javafx.stage.Stage
import javafx.stage.StageStyle
import org.wycliffeassociates.otter.jvm.app.theme.AppStyles
import org.wycliffeassociates.otter.jvm.app.ui.debugbrowser.view.DebugBrowserView
import org.wycliffeassociates.otter.jvm.app.ui.splash.view.SplashScreen
import tornadofx.*

class MyApp : App(SplashScreen::class) {
    init {
        importStylesheet<AppStyles>()
    }

    override fun start(stage: Stage) {
        stage.initStyle(StageStyle.TRANSPARENT)
        super.start(stage)
    }
}

class DebugBrowserApp : App(DebugBrowserView::class) {
    init {
        importStylesheet<AppStyles>()
    }

    override fun start(stage: Stage) {
        stage.initStyle(StageStyle.TRANSPARENT)
        super.start(stage)
    }
}

fun main(args: Array<String>) {
    if (args.size == 1 && args[0].contentEquals("debug")) {
        launch<DebugBrowserApp>(args)
    } else {
        launch<MyApp>(args)
    }
}
