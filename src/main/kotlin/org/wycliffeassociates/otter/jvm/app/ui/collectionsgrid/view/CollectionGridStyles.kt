package org.wycliffeassociates.otter.jvm.app.ui.collectionsgrid.view

import javafx.geometry.Pos
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
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
            backgroundColor += Color.WHITE
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
//            padding = box(0.px, 0.px, 0.px, 5.px) // Doesn't work
        }

        tabLabel {
            +tabWidthCenter
        }

        focusIndicator {
            focusColor = Color.TRANSPARENT
        }

//        tabHeaderBackground {
//            backgroundInsets = multi(box(0.px), box(0.px, 0.px, 0.px, 0.px), box(0.px))
//        }

        tab and selected {
            borderWidth += box(2.px, 0.px, 2.px, 0.px)
            borderColor += box(AppTheme.colors.appRed, Color.TRANSPARENT, Color.WHITE, Color.TRANSPARENT)
            backgroundColor += Color.WHITE
            faintFocusColor = Color.TRANSPARENT

            tabLabel {
                textFill = AppTheme.colors.appRed
                fontWeight = FontWeight.BOLD
            }
        }

        tabHeaderArea {
            padding = box(4.px, 0.px, 0.px, 25.px) // Doesn't add space between tabs
        }
    }
}