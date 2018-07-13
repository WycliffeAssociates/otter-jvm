package app.ui
import javafx.scene.paint.Color

import tornadofx.*
import widgets.profileIcon.view.ProfileIcon

class MainView : View() {
    override val root = stackpane {
//        displays outer circle(grey color)behind the button
        circle(radius = 100 * 1.1){
            fill = Color.LIGHTGRAY
        }
        add(ProfileIcon("01234567890"))
    }
}