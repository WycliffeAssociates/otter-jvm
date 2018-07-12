package widgets.profile.icon.view

import afester.javafx.svg.SvgLoader
import tornadofx.*
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Group
import jdenticon.Jdenticon
import widgets.profile.icon.view.Styles.Companion.OuterCircle
import widgets.profile.icon.view.Styles.Companion.profileIcon

class MyView : View() {

    override val root = vbox {

        stackpane {
            //displays outer circle(grey color)behind the button
            circle(radius = SVG_SIZE * 1.1) {
                addClass(OuterCircle)
            }
            button(graphic = svgGroup) {
                addClass(profileIcon)
                resizeSvg(svgGroup, SVG_SIZE*1.5, SVG_SIZE*1.5)
                onMouseClicked = EventHandler {
                    println("inner circle click")
                }
            }
        }
    }
}



fun resizeSvg(svgGroup: Group, width: Double, height: Double) {
    // adapted from https://stackoverflow.com/questions/38953921/how-to-set-the-size-of-a-svgpath-in-javafx
    val currentWidth = svgGroup.prefWidth(-1.0) // get the default preferred width
    val currentHeight = svgGroup.prefHeight(currentWidth) // get default preferred height

    svgGroup.scaleX = width / currentWidth * 0.60
    svgGroup.scaleY = height / currentHeight * 0.60
}


var SVG_SIZE: Int = 100
val SVG_STRING = "665995259578966"
val svgLoader = SvgLoader()
val svgGroup = svgLoader.loadSvg(Jdenticon.toSvg(hash = SVG_STRING, size = SVG_SIZE).byteInputStream())