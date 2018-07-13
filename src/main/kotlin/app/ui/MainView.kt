package app.ui
import widgets.profileIcon.view.ProfileIconStyle.Companion.OuterCircle

import tornadofx.*
import javafx.event.EventHandler
import widgets.profileIcon.view.ProfileIconStyle.Companion.ProfileIcon
import widgets.profileIcon.view.buttonSize
import widgets.profileIcon.view.resizeSvg
import widgets.profileIcon.view.svgGroup

var buttonWidth = 150.px
var buttonHeight = 150.px
class MainView : View() {
    override val root = stackpane {
//        displays outer circle(grey color)behind the button
        circle(radius = buttonSize * 1.1){
            addClass(OuterCircle)
        }
        button(graphic = svgGroup) {
            addClass(ProfileIcon)
            resizeSvg(svgGroup, buttonSize * 1.5)
            onMouseClicked = EventHandler {
                println("inner circle click")
            }
        }
    }
}