package org.wycliffeassociates.otter.jvm.app.ui.projectpage.view

import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import org.wycliffeassociates.otter.jvm.app.UIColorsObject.Colors
import tornadofx.*

class ProjectPageStylesheet : Stylesheet() {
    companion object {
        val contextualButton by cssclass()
        val chunkCard by cssclass()
        val disabledCard by cssclass()

        val recordCardButton by cssclass()
        val editCardButton by cssclass()
        val viewCardButton by cssclass()

        val whiteIcon by cssclass()
    }

    init {
        chunkCard {
            backgroundColor += c(Colors["base"])
            effect = DropShadow(10.0, Color.DARKGRAY)
            backgroundRadius += box(10.px)
            borderRadius = backgroundRadius

            disabledCard {
                backgroundColor += c(Colors["baseBackground"])
            }

            button {
                and(recordCardButton, editCardButton, viewCardButton) {
                    unsafe("-jfx-button-type", raw("RAISED"))
                    textFill = Color.WHITE
                    fontSize = 16.px
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



        whiteIcon {
            fill = Color.WHITE
        }
    }
}