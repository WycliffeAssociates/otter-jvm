package org.wycliffeassociates.otter.jvm.app.ui.cardgrid.view

import javafx.geometry.Pos
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
import tornadofx.*


class CardGridStyles: Stylesheet() {

    companion object {
        val contentContainer by cssclass()
        val contentLoadingProgress by cssclass()
    }

    init {
        contentContainer {
            cellHeight = 192.px
            cellWidth = 158.px
            alignment = Pos.CENTER
            horizontalCellSpacing = 16.0.px
            verticalCellSpacing = 8.0.px
            cell {
                backgroundColor += Color.TRANSPARENT
            }
        }

        contentLoadingProgress {
            progressColor = AppTheme.colors.appRed
        }
    }
}