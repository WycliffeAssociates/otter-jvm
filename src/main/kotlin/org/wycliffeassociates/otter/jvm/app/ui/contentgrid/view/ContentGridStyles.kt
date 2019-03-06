package org.wycliffeassociates.otter.jvm.app.ui.contentgrid.view

import javafx.geometry.Pos
import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
import tornadofx.*


class ContentGridStyles: Stylesheet() {

    companion object {
        val cardContentContainer by cssclass()
        val helpContentContainer by cssclass()
        val contentLoadingProgress by cssclass()
    }

    init {
        cardContentContainer {
            vgap = 32.px
            hgap = 24.px
            alignment = Pos.CENTER
            horizontalCellSpacing = 24.0.px
            verticalCellSpacing = 32.0.px
            // Add larger padding on bottom to keep FAB from blocking last row cards
        }

        helpContentContainer {
            alignment = Pos.TOP_LEFT
            maxCellsInRow = 1
            verticalCellSpacing = 32.0.px
            // Add larger padding on bottom to keep FAB from blocking last row cards
        }

        contentLoadingProgress {
            progressColor = AppTheme.colors.appRed
        }
    }
}