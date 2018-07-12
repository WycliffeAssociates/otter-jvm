package widgets.profile.icon.view

import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import tornadofx.*


class Styles : Stylesheet() {
    companion object {
        val jdenticonButton by cssclass()
        val profileIcon by cssclass()
        val OuterCircle by cssclass()
    }
    init {
        OuterCircle {
            fill = Color.LIGHTGRAY
        }
        profileIcon {
            backgroundColor += c("#ffffff")
            backgroundRadius += box(100.px)
            borderRadius += box(100.px)
            prefWidth = SVG_SIZE*1.5.px
            prefHeight = SVG_SIZE*1.5.px
            effect = DropShadow(10.0, Color.GRAY)
            and(hover) {
                opacity = 0.90
            }
        }
    }

}