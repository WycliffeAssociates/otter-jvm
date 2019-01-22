package org.wycliffeassociates.otter.jvm.app.widgets.versecard

import javafx.scene.layout.*
import tornadofx.*
import com.jfoenix.controls.JFXButton
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.effect.GaussianBlur
import javafx.scene.paint.Color
import org.wycliffeassociates.otter.common.data.model.Content

class VerseCard(verse: Content? = null) : AnchorPane() {

    var card: VBox by singleAssign()
//    var labelContainer: VBox by singleAssign()
    var cardLabel: Label by singleAssign()
    var cardNumber: Label by singleAssign()
    var cardButton: Button by singleAssign()
    var chapterGraphic: Node = StackPane()
    var cardBackground: VBox by singleAssign()
    private val defaultFill = c("#CC4141")

    init {
        importStylesheet<VerseCardStyles>()
        card = vbox(15) {
            alignment = Pos.CENTER
            vgrow = Priority.ALWAYS
            hgrow = Priority.ALWAYS
            stackpane {
                alignment = Pos.TOP_CENTER
                cardBackground = vbox {
                    hgrow = Priority.ALWAYS
                    addClass(VerseCardStyles.defaultCardBackground)
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
                            effect = GaussianBlur(15.0)
                        }
                        if (verse != null) {
                            cardLabel = label(verse.labelKey.toUpperCase())
                        }
                    }
                    stackpane {
                        ellipse {
                            centerX = 50.0
                            centerY = 0.0
                            radiusX = 30.0
                            radiusY = 30.0
                            fill = Color.WHITE
                            effect = GaussianBlur(15.0)
                            style { alignment = Pos.CENTER }
                        }
                        if (verse != null) {
                            cardNumber = label(verse.start.toString())
                        }
                    }

                }
            }
            cardButton = JFXButton("OPEN", MaterialIconView(MaterialIcon.ARROW_FORWARD).apply {
                fill = defaultFill
            })
            add(cardButton)
        }
    }
}

fun versecard(verse: Content? = null, init: VerseCard.() -> Unit = {}): VerseCard {
    val vc = VerseCard(verse)
    vc.init()
    return vc
}
