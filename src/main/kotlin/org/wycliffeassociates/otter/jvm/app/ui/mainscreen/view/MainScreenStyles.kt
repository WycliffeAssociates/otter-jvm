package org.wycliffeassociates.otter.jvm.app.ui.mainscreen

import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import javafx.stage.Screen
import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
import tornadofx.*

class MainScreenStyles: Stylesheet() {
    companion object {
        val main by cssclass()
        val listMenu by cssclass()
        val listItem by cssclass()
        val navBoxInnercard by cssclass()
        val navbutton by cssclass()
    }

    init {

        main {
            prefWidth = Screen.getPrimary().visualBounds.width.px - 20.0
            prefHeight = Screen.getPrimary().visualBounds.height.px - 20.0
        }

        // this gets compiled down to list-menu
        listMenu {
            backgroundColor += AppTheme.colors.defaultBackground
        }

        //this gets compiled down to list-item
        listItem {
            backgroundColor += AppTheme.colors.defaultBackground
            padding = box(24.px)

        }

        navBoxInnercard {
            backgroundColor += AppTheme.colors.lightBackground
            borderColor += box(Color.WHITE)
            borderWidth += box(3.0.px)
            borderRadius += box(5.0.px)
            borderInsets += box(1.5.px)
        }

        navbutton {
            backgroundColor += AppTheme.colors.white
            textFill = AppTheme.colors.defaultText
            borderColor += box(AppTheme.colors.lightBackground)
            backgroundRadius += box(25.px)
            borderRadius += box(25.px)
            effect = DropShadow(2.0,2.0, 2.0, AppTheme.colors.defaultBackground)
            prefWidth = 90.px
        }
    }
}