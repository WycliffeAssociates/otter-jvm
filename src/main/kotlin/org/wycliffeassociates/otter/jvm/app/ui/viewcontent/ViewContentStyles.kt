package org.wycliffeassociates.otter.jvm.app.ui.viewcontent

import javafx.geometry.Pos
import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
import tornadofx.*


class ViewContentStyles: Stylesheet() {

    companion object {
        val collectionsFlowpane by cssclass()
        val contentLoadingProgress by cssclass()
    }

    init {
        collectionsFlowpane {
            vgap = 32.px
            hgap = 24.px
            alignment = Pos.TOP_LEFT
            // Add larger padding on bottom to keep FAB from blocking last row cards
            padding = box(10.px, 20.px, 95.px, 20.px)
        }

        contentLoadingProgress {
            progressColor = AppTheme.colors.appRed
        }
    }
}