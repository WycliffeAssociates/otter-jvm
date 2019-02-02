package org.wycliffeassociates.otter.jvm.app.ui.mainscreen

import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
import tornadofx.*

class MainScreenStyles: Stylesheet() {
    companion object {
        val main by cssclass()
        val listMenu by cssclass()
        val listItem by cssclass()
    }

    init {

        main {

        }

        listMenu {
            backgroundColor += AppTheme.colors.defaultBackground
        }

        listItem {
            backgroundColor += AppTheme.colors.defaultBackground
            padding = box(24.px)

        }
        
    }
}