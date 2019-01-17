package org.wycliffeassociates.otter.jvm.app.widgets.chaptercard

import com.jfoenix.controls.JFXButton
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ProgressBar
import javafx.scene.effect.GaussianBlur
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.paint.Stop
import org.wycliffeassociates.otter.common.data.model.Collection
import tornadofx.*

class ChapterCard(chapter: Collection? = null) : AnchorPane() {

    var card: VBox by singleAssign()
    var cardLabel: Label by singleAssign()
    var cardNumber: Label by singleAssign()
    var cardButton: Button by singleAssign()
    var chapterGraphic: Node = StackPane()
    var cardBackground: VBox by singleAssign()
    var progressbar: ProgressBar by singleAssign()
    val stopList: List<Stop> = listOf(Stop(0.0, Color.WHITE), Stop(1.0, Color.color(1.0, 1.0, 1.0, 0.0)))

    init {
        card = vbox(15) {
            alignment = Pos.CENTER
            vgrow = Priority.ALWAYS
            hgrow = Priority.ALWAYS
            stackpane {
                alignment = Pos.TOP_CENTER
                cardBackground = vbox {
                    hgrow = Priority.ALWAYS
                    style {
                        prefWidth = 180.px
                        prefHeight = 70.px
                        maxHeight = 70.px
                        backgroundRadius += box(0.0.px, 0.0.px, 25.0.px, 25.0.px)
                    }
                }
                chapterGraphic = vbox {
                    alignment = Pos.CENTER
                    stackpane {
                        ellipse {
                            centerX = 0.0
                            centerY = 0.0
                            radiusX = 60.0
                            radiusY = 15.0
                            fill = Color.WHITE
//                        fill = RadialGradient(0.0,0.0,0.5,0.5,1.0,true,CycleMethod.NO_CYCLE, stopList)
                            effect = GaussianBlur(15.0)
                        }
                        if (chapter != null) {
                            cardLabel = label(chapter.labelKey.toUpperCase())
                        }
                    }

                    stackpane {
                        ellipse {
                            centerX = 50.0
                            centerY = 0.0
                            radiusX = 30.0
                            radiusY = 30.0
                            fill = Color.WHITE
//                        fill = RadialGradient(0.0,0.0,0.5,0.5,1.0,true,CycleMethod.NO_CYCLE, stopList)
                            effect = GaussianBlur(15.0)
                            style { alignment = Pos.CENTER }
                        }
                        if (chapter != null) {
                            cardNumber = label(chapter.titleKey)
                        }
                    }
                    progressbar = progressbar(0.2) {

                    }
                }
            }
            cardButton = JFXButton("OPEN", MaterialIconView(MaterialIcon.ARROW_FORWARD).apply {
                fill = c("#CC4141")
            })
            add(cardButton)
        }
    }
}

private fun percentComplete(collection: Collection) {
}

fun Pane.chaptercard(chapter: Collection? = null,
                     init: ChapterCard.() -> Unit = {}): ChapterCard {
    val chapterCard = ChapterCard(chapter)
    chapterCard.init()
    add(chapterCard)
    return chapterCard
}

