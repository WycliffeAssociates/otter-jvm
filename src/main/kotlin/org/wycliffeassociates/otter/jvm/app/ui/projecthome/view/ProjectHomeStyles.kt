package org.wycliffeassociates.otter.jvm.app.ui.projecthome.view

import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import javafx.scene.text.FontWeight
import javafx.stage.Screen
import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
import org.wycliffeassociates.otter.jvm.app.widgets.projectcard.ProjectCardStyles
import tornadofx.*

class ProjectHomeStyles : Stylesheet() {
    companion object {
        val homeAnchorPane by cssclass()
        val projectsFlowPane by cssclass()
        val noProjectsLabel by cssclass()
        val tryCreatingLabel by cssclass()
        val addProjectButton by cssclass()
        val projectCard by cssclass()
        val projectCardTitle by cssclass()
        val projectCardLanguage by cssclass()
        val projectGraphicContainer by cssclass()
    }

    init {
        homeAnchorPane {
            prefWidth = Screen.getPrimary().visualBounds.width.px - 50.0
            prefHeight = Screen.getPrimary().visualBounds.height.px - 50.0
        }

        projectsFlowPane {
            vgap = 32.px
            hgap = 24.px
            alignment = Pos.TOP_LEFT
            // Add larger padding on bottom to keep FAB from blocking last row cards
            padding = box(10.px, 20.px, 95.px, 20.px)
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
            backgroundColor += AppTheme.colors.white
            minHeight = 50.px
            minWidth = 50.px
            maxHeight = 50.px
            maxWidth = 50.px
            cursor = Cursor.HAND
            child("*") {
                fill = AppTheme.colors.appRed
            }
        }

        projectCard {
            prefWidth = 185.px
            prefHeight = 225.px
            backgroundColor += (LinearGradient(0.0,
                    0.0,
                    0.0,
                    80.0,
                    false,
                    CycleMethod.NO_CYCLE,
                    Stop(0.999, AppTheme.colors.appRed),
                    Stop(1.0, Color.WHITE)))
            padding = box(10.px)
            backgroundRadius += box(5.px)
            spacing = 10.px
            effect = DropShadow(2.0, 4.0, 6.0, Color.LIGHTGRAY)
            projectGraphicContainer {
                backgroundRadius += box(10.px)
                backgroundColor += c("#E6E8E9")
                effect = DropShadow(1.0, 1.0, 1.0, AppTheme.colors.defaultBackground)
                fill = AppTheme.colors.white
                child("*") {
                    fill = AppTheme.colors.defaultText
                }
                prefWidth = 188.0.px
                prefHeight = 150.14.px
                maxWidth = 188.0.px
                maxHeight = 150.14.px
                padding = box(1.0.px)

            }
            label {
                textFill = AppTheme.colors.subtitle
                and(projectCardTitle) {
                    fontWeight = FontWeight.BOLD
                    fontSize = 16.px
                }
                and(projectCardLanguage) {
                    fontWeight = FontWeight.NORMAL
                    textFill = AppTheme.colors.subtitle
                }
            }

            ProjectCardStyles.projectCardButton {
                minHeight = 40.px
                maxWidth = Double.MAX_VALUE.px
                backgroundColor += AppTheme.colors.appRed
                textFill = AppTheme.colors.white
                cursor = Cursor.HAND
                fontSize = 16.px
                fontWeight = FontWeight.BOLD
            }

            ProjectCardStyles.deleteProjectButton {
                s(".jfx-rippler") {
                    unsafe("-jfx-rippler-fill", raw(AppTheme.colors.appRed.css))
                }
                child("*") {
                    fill = AppTheme.colors.subtitle
                }
            }
        }
    }
}