package widgets.profileIcon.view

import app.ui.buttonHeight
import app.ui.buttonWidth
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import tornadofx.*


class ProfileIconStyle : Stylesheet() {
    companion object {
        val ProfileIcon by cssclass()
        val OuterCircle by cssclass()
    }
    init {
        OuterCircle {
            fill = Color.LIGHTGRAY
        }
        ProfileIcon {
            prefWidth = buttonWidth
            prefHeight = buttonHeight
            backgroundColor += c("#ffffff")
            backgroundRadius += box(100.px)
            borderRadius += box(100.px)
            effect = DropShadow(10.0, Color.GRAY)

            and(hover) {
                opacity = 0.90
            }
        }
    }
}