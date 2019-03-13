package org.wycliffeassociates.otter.jvm.app.ui.collectionsgrid.view

import javafx.geometry.Pos
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
import org.wycliffeassociates.otter.jvm.app.ui.mainscreen.view.MainScreenStyles.Companion.scripture
import org.wycliffeassociates.otter.jvm.app.ui.mainscreen.view.MainScreenStyles.Companion.translationNotes
import tornadofx.*

class CollectionGridStyles : Stylesheet() {

    companion object {
        val collectionsContainer by cssclass()
        val contentLoadingProgress by cssclass()
    }

    init {

        collectionsContainer {
            vgap = 32.px
            hgap = 24.px
            horizontalCellSpacing = 24.0.px
            verticalCellSpacing = 32.0.px
            alignment = Pos.CENTER

        }

        contentLoadingProgress {
            progressColor = AppTheme.colors.appRed
            backgroundColor += AppTheme.colors.workingAreaBackground
        }

        tabHeaderBackground {
            backgroundColor += Color.TRANSPARENT
        }

        val tabWidthCenter = mixin {
            alignment = Pos.CENTER
            prefWidth = 140.px // Must specify width for the label as well or center won't work
        }

        tab {
            +tabWidthCenter
            backgroundRadius += box(0.px, 0.px, 0.px, 0.px) // Remove beveled edges
            backgroundColor += AppTheme.colors.unselectedTabBackground
            borderWidth += box(0.px, 0.px, 2.px, 0.px)
//            padding = box(0.px, 0.px, 0.px, 5.px) // Doesn't work
        }

        tabLabel {
            +tabWidthCenter
        }

        focusIndicator {
            focusColor = Color.TRANSPARENT
        }

        tab and selected {
            borderWidth += box(2.px, 0.px, 2.px, 0.px)
            backgroundColor += AppTheme.colors.selectedTabBackground
            faintFocusColor = Color.TRANSPARENT

            tabLabel {
                fontWeight = FontWeight.BOLD
            }

            and(scripture) {
                borderColor += box(
                        AppTheme.colors.appRed,
                        Color.TRANSPARENT,
                        AppTheme.colors.selectedTabBackground,
                        Color.TRANSPARENT
                )
                tabLabel {
                    textFill = AppTheme.colors.appRed
                }
            }

            and(translationNotes) {
                borderColor += box(
                        AppTheme.colors.appOrange,
                        Color.TRANSPARENT,
                        AppTheme.colors.selectedTabBackground,
                        Color.TRANSPARENT
                )
                tabLabel {
                    textFill = AppTheme.colors.appOrange
                }
            }
        }

        tabHeaderArea {
            padding = box(4.px, 0.px, 0.px, 25.px) // Doesn't add space between tabs
        }
    }
}