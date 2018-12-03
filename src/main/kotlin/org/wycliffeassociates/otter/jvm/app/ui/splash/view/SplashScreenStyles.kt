package org.wycliffeassociates.otter.jvm.app.ui.splash.view

import javafx.geometry.Pos
import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
import tornadofx.Stylesheet
import tornadofx.cssclass
import tornadofx.px

class SplashScreenStyles : Stylesheet() {
    companion object {
        val splashRoot by cssclass()
        val splashProgress by cssclass()
    }

    init {
        splashRoot {
            backgroundColor += AppTheme.colors.defaultBackground
            prefWidth = 300.px
            prefHeight = 200.px
            alignment = Pos.CENTER
            splashProgress {
                prefWidth = 250.px
                track {
                    backgroundColor += AppTheme.colors.base
                }
                bar {
                    accentColor = AppTheme.colors.appRed
                }
            }
        }
    }
}