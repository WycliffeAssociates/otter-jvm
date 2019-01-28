package org.wycliffeassociates.otter.jvm.app.widgets

import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.effect.DropShadow
import javafx.scene.layout.BackgroundPosition
import javafx.scene.layout.BackgroundRepeat
import javafx.scene.layout.BackgroundSize
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
import org.wycliffeassociates.otter.jvm.app.ui.projecteditor.view.ProjectEditorStyles
import tornadofx.*
import java.net.URI

class DefaultStyles : Stylesheet() {
    private val defaultRed = c("#CC4141")
    private val defaultWhite = c("#FFFF")

    companion object {
        val defaultBaseTop by cssclass()
        val baseBottom by cssclass()
        val defaultInnerCard by cssclass()
        val defaultCard by cssclass()
        val defaultCardButton by cssclass()
        val defaultCardProgressBar by cssclass()
    }

    init {
        defaultBaseTop {
            prefWidth = Double.MAX_VALUE.px
            prefHeight = 70.px
            maxHeight = 70.px
            backgroundRadius += box(0.0.px, 0.0.px, 25.0.px, 25.0.px)
            backgroundColor += defaultRed
        }

        baseBottom {
            backgroundColor += defaultWhite
            backgroundRadius += box(5.px)
        }

        defaultInnerCard {
            backgroundColor += Color.LIGHTGRAY
            borderColor += box(Color.ALICEBLUE)
            borderWidth += box(1.0.px)
            backgroundImage += URI("/images/project_image.png")
            backgroundRepeat += Pair(BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT)
            backgroundPosition += BackgroundPosition.CENTER
            backgroundSize += BackgroundSize(90.0,
                    90.0,
                    true,
                    true,
                    true,
                    true)
            prefHeight = 118.px
            prefWidth = 142.px
            backgroundRadius += box(5.px)

            label {
                textFill = defaultWhite
            }
            padding = box(2.px)
        }

        defaultCard {
            backgroundColor += Color.WHITE
            prefHeight = 192.px
            prefWidth = 158.px
            borderRadius += box(5.px)
            backgroundRadius += box(5.px)
            spacing = 10.px
            effect = DropShadow(2.0, 4.0, 6.0, AppTheme.colors.lightBackground)
        }

        defaultCardButton {
            alignment = Pos.CENTER
            maxHeight = 40.px
            maxWidth = 168.px
            borderColor += box(AppTheme.colors.appRed)
            borderRadius += box(5.0.px)
            backgroundColor += defaultWhite
            textFill = defaultRed
            cursor = Cursor.HAND
            fontSize = 16.px
            fontWeight = FontWeight.BOLD
        }

        defaultCardProgressBar {
            maxWidth = 118.px
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