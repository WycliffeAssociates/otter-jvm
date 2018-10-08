package org.wycliffeassociates.otter.jvm.app.ui.viewtakes.view

import javafx.geometry.Pos
import javafx.scene.paint.Color
import org.wycliffeassociates.otter.jvm.app.UIColorsObject.Colors
import tornadofx.Stylesheet
import tornadofx.*

class ViewTakesStylesheet : Stylesheet() {
    companion object {
        val backButton by cssclass()
        val acceptButton by cssclass()
        val rejectButton by cssclass()
        val actionButtonsContainer by cssclass()
        val dragTarget by cssclass()
        val takeCard by cssclass()
        val badge by cssclass("badge")
    }

    init {
        button {
           and(backButton) {
               minWidth = 230.px
               textFill = Color.WHITE
               child("*") {
                   fill = Color.WHITE
               }
               backgroundColor += c(Colors["primary"])
               unsafe("-jfx-button-type", raw("RAISED"))
           }

            and(acceptButton, rejectButton) {
                padding = box(5.px, 30.px)
                backgroundRadius += box(5.px)
                borderRadius += box(5.px)
                borderColor += box(c(Colors["primary"]))
            }

            and(acceptButton) {
                backgroundColor += c(Colors["primary"])
                child("*") {
                    fill = Color.WHITE
                }
            }

            and(rejectButton) {
                backgroundColor += Color.WHITE
                child("*") {
                    fill = c(Colors["primary"])
                }
            }

        }

        actionButtonsContainer {
            alignment = Pos.CENTER
        }

        dragTarget {
            backgroundColor += c(Colors["baseBackground"])
            borderRadius += box(10.px)
            backgroundRadius += box(10.px)

            label {
                fontSize = 16.px
            }
        }

        takeCard {
            borderColor += box(Color.BLACK)
            borderWidth += box(1.px)
            borderRadius += box(10.px)

            badge {
                backgroundColor += c(Colors["primary"])
            }
        }

    }
}