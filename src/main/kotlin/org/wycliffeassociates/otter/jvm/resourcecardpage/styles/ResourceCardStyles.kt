package org.wycliffeassociates.otter.jvm.resourcecardpage.styles

import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import javafx.scene.text.FontWeight
import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
import tornadofx.*

typealias LinearU = Dimension<Dimension.LinearUnits>

class ResourceCardStyles : Stylesheet() {

    companion object {
        val statusBarBanner by cssclass()
        val resourceCard by cssclass()
        val resourceGroupCard by cssclass()
        val viewRecordingsButton by cssclass()
        val gridIcon by cssclass()
        val resourceGroupList by cssclass()
    }

    init {
        statusBarBanner {
            padding = box(10.px, 100.px, 20.px, 70.px)
            backgroundColor += AppTheme.colors.white
            // Somehow, there is a border underneath the status bar banner
//            borderColor += box(Color.TRANSPARENT, Color.TRANSPARENT, AppTheme.colors.appRed, Color.TRANSPARENT)
        }

        resourceCard {
        }

        resourceGroupCard {
            spacing = 10.px // VBox spacing
            padding = box(15.px)
            backgroundColor += AppTheme.colors.white
            effect = DropShadow(2.0, 2.0, 4.0, AppTheme.colors.lightBackground)
            backgroundRadius += box(5.px) // No border, so background needs to be rounded
            label {
                fontWeight = FontWeight.BOLD
            }
        }

        // TODO: This shares a lot with DefaultStyles.defaultCardButton
        viewRecordingsButton {
            alignment = Pos.CENTER
            maxHeight = 40.px
            maxWidth = 250.px
            backgroundColor += AppTheme.colors.white
            borderRadius += box(5.0.px)
//            borderColor += box(AppTheme.colors.white)
            textFill = AppTheme.colors.appOrange
            cursor = Cursor.HAND
            fontSize = 16.px
            fontWeight = FontWeight.BOLD

            gridIcon {
                fill = AppTheme.colors.appOrange // TODO don't hardcode colors (also for hover)
            }

            and(hover) {
//                borderColor += box(AppTheme.colors.appOrange)
                textFill = AppTheme.colors.white
                backgroundColor += buttonLinearGradient(
                        AppTheme.colors.appLightOrange,
                        AppTheme.colors.appOrange
                )
                gridIcon {
                    fill = AppTheme.colors.white
                }
            }
        }

        resourceGroupList {
            borderColor += box(Color.TRANSPARENT) // Necessary for border under status bar banner to stay visible
            padding = box(0.px, 0.px, 0.px, 80.px) // Left "margin"
            scrollBar {
                +margin(0.px, 0.px, 0.px, 80.px) // Margin between scrollbar and right side of cards
            }

            listCell {
                // Add space between the cards (top margin)
                // But need to make the "margin" at least as large as the dropshadow offsets
                +margin(30.px, 4.px, 0.px, 0.px)
            }
        }
    }

    private fun margin(top: LinearU, right: LinearU, bottom: LinearU, left: LinearU) = mixin {
        padding = box(top, right, bottom, left)
        backgroundInsets += box(top, right, bottom, left)
//            borderInsets += box(top, right, bottom, left)
    }

    private fun buttonLinearGradient(startColor: Color, endColor: Color): LinearGradient =
            LinearGradient(
                    0.0,
                    0.0,
                    0.0,
                    1.0,
                    true,
                    CycleMethod.NO_CYCLE,
                    Stop(0.0, startColor),
                    Stop(1.0, endColor)
            )
}