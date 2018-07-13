package widgets.profileIcon.view

import afester.javafx.svg.SvgLoader
import tornadofx.*
import javafx.event.EventHandler
import javafx.scene.Group
import javafx.scene.layout.StackPane
import jdenticon.Jdenticon
import widgets.profileIcon.view.ProfileIconStyle.Companion.OuterCircle
import widgets.profileIcon.view.ProfileIconStyle.Companion.ProfileIcon

//todo: ask Matthew how to make the button a class that can be mentioned in MainView
//class ProfileIcon(svgHash: String, var buttonSize: Double= 100.0): View() {
//    init{
//        val but = button(graphic = svgGroup) {
//            addClass(ProfileIcon)
//            resizeSvg(svgGroup, SVG_SIZE * 1.5, SVG_SIZE * 1.5)
//            onMouseClicked = EventHandler {
//                println("inner circle click")
//            }
//        }
//        add(but())
//    }
//}

//    init {
//        val button = stackpane {
//            //displays outer circle(grey color)behind the button
//            circle(radius = SVG_SIZE * 1.1) {
//                addClass(OuterCircle)
//            }
//            button() {
//                addClass(ProfileIcon)
//                resizeSvg(svgGroup, SVG_SIZE*1.5, SVG_SIZE*1.5)
//                onMouseClicked = EventHandler {
//                    println("inner circle click")
//                }
//            }
//            add(button())
//        }
//    }
//}

//todo: delete svgsize and hash declaration once the function have those in the parameters
var buttonSize: Double = 100.0
val hash = "665995259578966"
val svgGroup = SvgLoader().loadSvg(Jdenticon.toSvg(hash = hash, size = buttonSize.toInt()).byteInputStream())


fun resizeSvg(svgGroup: Group, size: Double = buttonSize) {
    // adapted from https://stackoverflow.com/questions/38953921/how-to-set-the-size-of-a-svgpath-in-javafx
    val currentWidth = svgGroup.prefWidth(-1.0) // get the default preferred width
    val currentHeight = svgGroup.prefHeight(currentWidth) // get default preferred height

    svgGroup.scaleX = size / currentWidth * 0.60
    svgGroup.scaleY = size / currentHeight * 0.60
}