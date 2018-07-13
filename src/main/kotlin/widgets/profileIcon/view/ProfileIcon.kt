package widgets.profileIcon.view

import afester.javafx.svg.SvgLoader
import tornadofx.*
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.layout.StackPane
import jdenticon.Jdenticon
import widgets.profileIcon.view.ProfileIconStyle.Companion.ProfileIcon

//todo: ask Matthew how to make the button a class that can be mentioned in MainView
class ProfileIcon(svgHash: String, var buttonSize: Double= 150.0): StackPane() {
    var svgGroup = SvgLoader().loadSvg(Jdenticon.toSvg(hash = svgHash, size = buttonSize.toInt()).byteInputStream())
    init{
        val profIcon = button(graphic = svgGroup) {

            addClass(ProfileIcon)
            prefWidth = buttonSize
            prefHeight = buttonSize
            resizeSvg(svgGroup, buttonSize)
            onMouseClicked = EventHandler {
                println("inner circle click")
            }
        }
        add(profIcon)
    }


    fun resizeSvg(svgGroup: Group, size: Double = buttonSize) {
        // adapted from https://stackoverflow.com/questions/38953921/how-to-set-the-size-of-a-svgpath-in-javafx
        val currentWidth = svgGroup.prefWidth(-1.0) // get the default preferred width
        val currentHeight = svgGroup.prefHeight(currentWidth) // get default preferred height

        svgGroup.scaleX = size / currentWidth * 0.70
        svgGroup.scaleY = size / currentHeight * 0.70
    }
}