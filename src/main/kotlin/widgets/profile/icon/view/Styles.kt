package widgets.profile.icon.view

import javafx.scene.paint.Paint
import javafx.scene.text.FontWeight
import tornadofx.*


class Styles : Stylesheet() {
    companion object {
        val jdenticonButton by cssclass()
        val profileIcon by cssclass()
    }
    init {
        profileIcon {
            backgroundColor += c("#a94442")
            borderRadius += box(100.px)
        }
        jdenticonButton {
            backgroundColor += c("#a94442")
            prefWidth = 300.px
            prefHeight = 300.px
        }

        label {
            fontSize = 20.px
            fontWeight = FontWeight.BOLD
            backgroundColor += c("#cecece")
        }
    }

}