package org.wycliffeassociates.otter.jvm.app.ui.projectgrid.view

import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import javafx.stage.Screen
import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
import tornadofx.*

class ProjectGridStyles : Stylesheet() {
    companion object {
        val projectsGrid by cssclass()
        val noProjectsLabel by cssclass()
        val tryCreatingLabel by cssclass()
        val addProjectButton by cssclass()
    }

    init {

        projectsGrid {
            cellHeight = 192.px
            cellWidth = 158.px
            cell {
                backgroundColor += Color.TRANSPARENT
            }
            alignment = Pos.CENTER
            horizontalCellSpacing = 16.0.px
            verticalCellSpacing = 8.0.px
            // Add larger padding on bottom to keep FAB from blocking last row cards
            padding = box(0.px, 0.px, 95.px, 0.px)
        }

        noProjectsLabel {
            fontSize = 30.px
            fontWeight = FontWeight.BOLD
            textFill = AppTheme.colors.defaultText
        }

        tryCreatingLabel {
            fontSize = 20.px
            textFill = AppTheme.colors.defaultText
        }

        addProjectButton {
            unsafe("-jfx-button-type", raw("RAISED"))
            backgroundRadius += box(25.px)
            borderRadius += box(25.px)
            backgroundColor += AppTheme.colors.appRed
            minHeight = 50.px
            minWidth = 50.px
            maxHeight = 50.px
            maxWidth = 50.px
            cursor = Cursor.HAND
            child("*") {
                fill = AppTheme.colors.white
            }
        }
    }
}