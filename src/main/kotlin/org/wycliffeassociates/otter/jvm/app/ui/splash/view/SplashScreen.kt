package org.wycliffeassociates.otter.jvm.app.ui.splash.view

import org.wycliffeassociates.otter.jvm.app.ui.splash.viewmodel.SplashScreenViewModel
import tornadofx.*

class SplashScreen : View() {
    private val viewModel: SplashScreenViewModel by inject()
    override val root = vbox {
        addClass(SplashScreenStyles.splashRoot)
        progressbar(viewModel.progressProperty) {
            addClass(SplashScreenStyles.splashProgress)
        }
    }

    init {
        importStylesheet<SplashScreenStyles>()
        viewModel.shouldCloseProperty.onChange {
            if (it) close()
        }
    }
}