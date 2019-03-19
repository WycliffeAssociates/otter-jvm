package org.wycliffeassociates.otter.jvm.app.ui.helpcontentlist.view

import javafx.geometry.Pos
import javafx.scene.text.FontPosture
import javafx.scene.text.FontWeight
import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
import tornadofx.*


class HelpContentListStyles: Stylesheet() {

    companion object {
        val helpContentContainer by cssclass()
        val helpTitle by cssclass()
        val contentLoadingProgress by cssclass()
    }

    init {
        helpContentContainer {
            alignment = Pos.TOP_LEFT
            maxCellsInRow = 1
            verticalCellSpacing = 32.0.px
            // Add larger padding on bottom to keep FAB from blocking last row cards
        }

        helpTitle {
            fontWeight = FontWeight.BOLD
        }

        contentLoadingProgress {
            progressColor = AppTheme.colors.appRed
        }
    }
}