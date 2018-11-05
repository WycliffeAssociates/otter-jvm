package org.wycliffeassociates.otter.jvm.app.ui.addplugin

import javafx.scene.text.FontWeight
import org.wycliffeassociates.otter.jvm.app.UIColorsObject.Colors
import tornadofx.*

class AddPluginStyles : Stylesheet() {
    companion object {
        val saveButton by cssclass()
    }

    init {
        form {
            backgroundColor += c(Colors["base"])
        }
        button {
            unsafe("-jfx-button-type", raw("FLAT"))
            backgroundColor += c(Colors["baseMedium"])
            fontWeight = FontWeight.BOLD
            textFill = c(Colors["primary"])
            padding = box(10.px, 20.px)
            and(saveButton) {
                textFill = c(Colors["base"])
                backgroundColor += c(Colors["primary"])
                unsafe("-jfx-button-type", raw("RAISED"))
                and(disabled) {
                    unsafe("-jfx-button-type", raw("FLAT"))
                }
            }
        }
    }
}