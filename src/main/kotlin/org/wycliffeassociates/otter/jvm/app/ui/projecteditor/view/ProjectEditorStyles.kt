package org.wycliffeassociates.otter.jvm.app.ui.projecteditor.view

import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.effect.Bloom
import javafx.scene.effect.DropShadow
import javafx.scene.layout.BackgroundPosition
import javafx.scene.layout.BackgroundRepeat
import javafx.scene.layout.BackgroundSize
import javafx.scene.paint.Color
import javafx.scene.paint.CycleMethod
import javafx.scene.paint.LinearGradient
import javafx.scene.paint.Stop
import javafx.scene.text.FontWeight
import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
import tornadofx.*
import java.net.URI

class ProjectEditorStyles : Stylesheet() {
    companion object {
        val contentCard by cssclass()
        val disabledCard by cssclass()

        val recordContext by cssclass()
        val hasTakes by cssclass()
        val editContext by cssclass()
        val viewContext by cssclass()

        val recordMenuItem by cssclass()
        val editMenuItem by cssclass()
        val viewMenuItem by cssclass()

        val projectTitle by cssclass()
        val contentGridContainer by cssclass()

        val active by csspseudoclass("active")

        val chapterList by cssclass()

        val contentLoadingProgress by cssclass()

        val backButtonContainer by cssclass()
        val contextMenu by cssclass()

        val collectionsFlowpane by cssclass()

        val collectionCard by cssclass()
        val chaptercardGraphic by cssclass()
        val versecardGraphic by cssclass()
        val cardLabel by cssclass()
        val cardNumber by cssclass()
        val cardButton by cssclass()
        val cardLabelContainer by cssclass()
        val cardBackground by cssclass()
        val cardProgressbar by cssclass()

    }

    init {
        projectTitle {
            fontSize = 20.px
            padding = box(10.px)
            backgroundColor += AppTheme.colors.imagePlaceholder
            textFill = AppTheme.colors.defaultText
            maxWidth = Double.MAX_VALUE.px
            alignment = Pos.BOTTOM_LEFT
            prefHeight = 100.px
        }

        contentGridContainer {
            padding = box(0.px, 20.px)
        }

        collectionCard{
            prefWidth = 180.px
            prefHeight = 210.px
            maxWidth = 180.px
            maxHeight = 210.px
            backgroundColor += Color.WHITE
            backgroundRadius += box(5.0.px)
            padding = box(0.0.px, 0.0.px,10.0.px, 0.0.px)
            effect = DropShadow(2.0, 4.0, 6.0, Color.LIGHTGRAY)

            cardBackground {
                backgroundColor += c("#E6E8E9")
            }
            alignment = Pos.CENTER

            chaptercardGraphic{
                prefWidth = 160.0.px
                prefHeight = 140.14.px
                maxWidth = 160.0.px
                maxHeight = 140.14.px
                backgroundColor += (LinearGradient(0.0,0.0,0.0,140.0,false, CycleMethod.NO_CYCLE, Stop(0.0, Color.WHITE), Stop(1.0, Color.LIGHTGRAY)))
                backgroundRadius += box(5.0.px)
                borderColor += box(Color.WHITE)
                borderWidth += box(3.0.px)
                borderRadius += box(5.0.px)
                borderInsets += box(1.5.px)
                backgroundImage += URI("/images/chapter_image.png")
                backgroundRepeat += Pair(BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT)
                backgroundPosition += BackgroundPosition.CENTER
                backgroundSize += BackgroundSize(0.0, 0.0,false, false, true, true)
                fontSize = 12.px
            }
            versecardGraphic {
                prefWidth = 160.0.px
                prefHeight = 140.14.px
                maxWidth = 160.0.px
                maxHeight = 140.14.px
                backgroundColor += (LinearGradient(0.0,0.0,0.0,140.0,false, CycleMethod.NO_CYCLE, Stop(0.0, Color.WHITE), Stop(1.0, Color.LIGHTGRAY)))
                backgroundRadius += box(5.0.px)
                borderColor += box(Color.WHITE)
                borderWidth += box(3.0.px)
                borderRadius += box(5.0.px)
                borderInsets += box(1.5.px)
                backgroundImage += URI("/images/verse_image.png")
                backgroundRepeat += Pair(BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT)
                backgroundPosition += BackgroundPosition.CENTER
                backgroundSize += BackgroundSize(0.0, 0.0,false, false, true, true)
                fontSize = 16.px
            }
            cardLabel {
                effect = DropShadow(25.0, 2.0,2.0, c("#FBFEFF"))
            }
            cardNumber{
                fontSize = 46.px
                fontWeight = FontWeight.BOLD
                effect = Bloom(0.2)
            }
            cardButton {
                alignment = Pos.CENTER
                maxHeight = 40.px
                maxWidth = 168.px
                borderColor += box(AppTheme.colors.appRed)
                borderRadius += box(5.0.px)
                backgroundColor += AppTheme.colors.white
                textFill = AppTheme.colors.appRed
                cursor = Cursor.HAND
                fontSize = 16.px
                fontWeight = FontWeight.BOLD
            }
            cardLabelContainer {
                fontSize = 16.0.px
                alignment = Pos.CENTER
            }
        }

        collectionsFlowpane {
                vgap = 32.px
                hgap = 24.px
                alignment = Pos.TOP_LEFT
                // Add larger padding on bottom to keep FAB from blocking last row cards
                padding = box(10.px, 20.px, 95.px, 20.px)
        }

        contentCard {
            backgroundColor += AppTheme.colors.cardBackground
            effect = DropShadow(10.0, AppTheme.colors.dropShadow)

            label {
                textFill = AppTheme.colors.defaultText
                child("*") {
                    fill = AppTheme.colors.defaultText
                }
            }

            and(disabledCard) {
                backgroundColor += AppTheme.colors.disabledCardBackground
            }

            and(recordContext) {
                button {
                    backgroundColor += AppTheme.colors.appRed
                }
                and(hasTakes) {
                    button {
                        backgroundColor += AppTheme.colors.cardBackground
                        borderRadius += box(3.px)
                        borderColor += box(AppTheme.colors.appRed)
                        textFill = AppTheme.colors.appRed
                        child("*") {
                            fill = AppTheme.colors.appRed
                        }
                    }
                }
            }
            and(viewContext) {
                button {
                    backgroundColor += AppTheme.colors.appBlue
                }
            }
            and(editContext) {
                button {
                    backgroundColor += AppTheme.colors.appGreen
                }
            }
        }

        s(recordMenuItem, viewMenuItem, editMenuItem) {
            padding = box(20.px)
            backgroundColor += AppTheme.colors.base
            and(hover, active) {
                child("*") {
                    fill = AppTheme.colors.white
                }
            }
        }

        contextMenu {
            padding = box(0.px)
        }

        recordMenuItem {
            and(hover, active) {
                backgroundColor += AppTheme.colors.appRed
            }
            child("*") {
                fill = AppTheme.colors.appRed
            }
        }

        viewMenuItem {
            and(hover, active) {
                backgroundColor += AppTheme.colors.appBlue
            }
            child("*") {
                fill = AppTheme.colors.appBlue
            }
        }

        editMenuItem {
            and(hover, active) {
                backgroundColor += AppTheme.colors.appGreen
            }
            child("*") {
                fill = AppTheme.colors.appGreen
            }
        }

        chapterList {
            focusColor = Color.TRANSPARENT
            faintFocusColor = Color.TRANSPARENT
            borderWidth += box(0.px)
            padding = box(10.px, 0.px, 0.px, 10.px)
            backgroundColor += AppTheme.colors.base
            listCell {
                padding = box(0.px, 0.px, 0.px, 20.px)
                backgroundColor += AppTheme.colors.base
                backgroundRadius += box(10.px)
                fontSize = 14.px
                fontWeight = FontWeight.BOLD
                prefHeight = 40.px
                label {
                    textFill = AppTheme.colors.defaultText
                    child("*") {
                        fill = AppTheme.colors.defaultText
                    }
                }

                and(hover) {
                    backgroundColor += AppTheme.colors.defaultBackground
                }
                and(selected) {
                    backgroundColor += AppTheme.colors.appRed
                    label {
                        textFill = AppTheme.colors.white
                        child("*") {
                            fill = AppTheme.colors.white
                        }
                    }
                }
            }
        }

        contentLoadingProgress {
            progressColor = AppTheme.colors.appRed
        }

        backButtonContainer {
            alignment = Pos.CENTER_RIGHT
            spacing = 20.px
        }

        cardProgressbar {
            maxWidth = (Double.MAX_VALUE - 10.0).px
            track {
                backgroundColor += AppTheme.colors.base
            }
            bar {
                padding = box(4.px)
                backgroundInsets += box(0.px)
                accentColor = AppTheme.colors.appBlue
                backgroundRadius += box(0.px)
            }
        }

    }
}