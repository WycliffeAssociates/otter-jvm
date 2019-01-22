package org.wycliffeassociates.otter.jvm.app.widgets.projectnav

import javafx.geometry.Pos
import javafx.scene.effect.Bloom
import javafx.scene.effect.DropShadow
import javafx.scene.layout.BackgroundPosition
import javafx.scene.layout.BackgroundRepeat
import javafx.scene.layout.BackgroundSize
import javafx.scene.paint.Color
import javafx.scene.text.FontWeight
import tornadofx.*
import java.net.URI

class ProjectNavStyles: Stylesheet() {

    companion object {
        val projectNavCard by cssclass()
        val chapterNavCard by cssclass()
        val chunkNavCard by cssclass()
        val cardLabel by cssclass()
    }

    init {
        projectNavCard{
            maxWidth = 170.0.px
            maxHeight = 150.px
            prefHeight = 150.px
            backgroundColor += c("#E6E8E9")
            backgroundRadius += box(5.0.px)
            alignment = Pos.CENTER
            backgroundImage += URI("/images/project_image.png")
            backgroundRepeat += Pair(BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT)
            backgroundPosition += BackgroundPosition.CENTER
            backgroundSize += BackgroundSize(0.0, 0.0,false, false, true, true)
            fontSize = 16.px
            fontWeight = FontWeight.BOLD
            effect = Bloom(0.2)
            textFill = Color.WHITE
            alignment = Pos.BOTTOM_CENTER
            child("*") {
                textFill = Color.WHITE
            }
        }

        chapterNavCard {
            maxWidth = 170.0.px
            maxHeight = 150.px
            prefHeight = 150.px
            backgroundColor += c("#E6E8E9")
            backgroundRadius += box(5.0.px)
            alignment = Pos.CENTER
            backgroundImage += URI("/images/chapter_image.png")
            backgroundRepeat += Pair(BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT)
            backgroundPosition += BackgroundPosition.CENTER
            backgroundSize += BackgroundSize(0.0, 0.0,false, false, true, true)
            fontSize = 24.px
            fontWeight = FontWeight.BOLD
            effect = Bloom(0.2)
            textFill = Color.WHITE
            alignment = Pos.CENTER
        }

        chunkNavCard {
            maxWidth = 170.0.px
            maxHeight = 150.px
            prefHeight = 150.px
            backgroundColor += c("#E6E8E9")
            backgroundRadius += box(5.0.px)
            alignment = Pos.CENTER
            backgroundImage += URI("/images/verse_image.png")
            backgroundRepeat += Pair(BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT)
            backgroundPosition += BackgroundPosition.CENTER
            backgroundSize += BackgroundSize(0.0, 0.0,false, false, true, true)
            fontSize = 24.px
            fontWeight = FontWeight.BOLD
            effect = Bloom(0.2)
            textFill = Color.WHITE
            alignment = Pos.CENTER
        }

        cardLabel {
            effect = DropShadow(25.0, 2.0,2.0, c("#FBFEFF"))
            fontSize = 24.px
        }


    }
}