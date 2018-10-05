package org.wycliffeassociates.otter.jvm.app.ui.projectpage.view

import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import org.wycliffeassociates.otter.jvm.app.UIColorsObject.Colors
import tornadofx.*

class ProjectPageStylesheet : Stylesheet() {
    companion object {
        val chunkCard by cssclass()
        val disabledCard by cssclass()
        val titleLabel by cssclass("title")
        val selectedTakeLabel by cssclass("selected-take")

        val recordCardButton by cssclass()
        val editCardButton by cssclass()
        val viewCardButton by cssclass()

        val recordMenuItem by cssclass()
        val editMenuItem by cssclass()
        val viewMenuItem by cssclass()

        val listMenuIcon by cssclass()
        val whiteIcon by cssclass()

        val active by csspseudoclass("active")
    }

    init {
        datagrid {
            cellWidth = 200.px
            cellHeight = 200.px
            cell {
                backgroundColor += Color.TRANSPARENT
            }
        }
        chunkCard {
            backgroundColor += c(Colors["base"])
            effect = DropShadow(10.0, Color.DARKGRAY)
            backgroundRadius += box(10.px)
            borderRadius += box(10.px)
            padding = box(10.px)

            and(disabledCard) {
                backgroundColor += c(Colors["baseBackground"])
            }

            titleLabel {
                fontSize = 20.px
            }

            selectedTakeLabel {
                fontSize = 15.px
            }

            button {
                and(recordCardButton, editCardButton, viewCardButton) {
                    unsafe("-jfx-button-type", raw("RAISED"))
                    textFill = Color.WHITE
                    fontSize = 16.px
                    child("*") {
                        fill = Color.WHITE
                    }
                    maxWidth = Double.MAX_VALUE.px
                    fillWidth = true
                }
                and(recordCardButton) {
                    backgroundColor += c(Colors["primary"])
                }
                and(viewCardButton) {
                    backgroundColor += c(Colors["secondary"])
                }
                and(editCardButton) {
                    backgroundColor += c(Colors["tertiary"])
                }

            }
        }

        s(recordMenuItem, viewMenuItem, editMenuItem) {
            padding = box(20.px)
            backgroundColor += Color.WHITE
            and(hover, active) {
                child("*") {
                    fill = Color.WHITE
                }
            }
        }

        recordMenuItem {
            and(hover, active) {
                backgroundColor += c(Colors["primary"])
            }
            child("*") {
                fill = c(Colors["primary"])
            }
        }

        viewMenuItem {
            and(hover, active) {
                backgroundColor += c(Colors["secondary"])
            }
            child("*") {
                fill = c(Colors["secondary"])
            }
        }

        editMenuItem {
            and(hover, active) {
                backgroundColor += c(Colors["tertiary"])
            }
            child("*") {
                fill = c(Colors["tertiary"])
            }
        }
    }
}