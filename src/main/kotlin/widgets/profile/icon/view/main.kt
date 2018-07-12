package widgets.profile.icon.view


import afester.javafx.svg.SvgLoader
import tornadofx.*
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.Group
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import jdenticon.Jdenticon
import java.io.ByteArrayInputStream
import java.io.InputStream

var svgString1 = "M8 8L1 8L1 4ZM22 8L22 1L25 1ZM22 22L29 22L29 25ZM8 22L8 29L4 29Z"
var svgString2 = "M10 10L15 10L15 15L10 15ZM20 10L20 15L15 15L15 10ZM20 20L15 20L15 15L20 15ZM10 20L10 15L15 15L15 20Z"
var svgString3 = "M15 1L15 8L8 8ZM22 8L15 8L15 1ZM15 29L15 22L22 22ZM8 22L15 22L15 29ZM8 8L8 15L1 15ZM29 15L22 15L22 8ZM22 22L22 15L29 15ZM1 15L8 15L8 22Z"
var svgSize = 100.0
var svgOpacity = 1.0
var test2 = Jdenticon.toSvg(hash = "12875865435", size = 100)
val input = "test2"
val inputStream = ByteArrayInputStream(input.toByteArray(Charsets.UTF_8))

class MyView : View() {
    //
//        val SVG_STRING = "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"30\" height=\"30\" viewBox=\"0 0 30 30\" preserveAspectRatio=\"xMidYMid meet\">\n" +
//                "<path fill=\"#b2e5b6\" d=\"M8 8L1 8L1 4ZM22 8L22 1L25 1ZM22 22L29 22L29 25ZM8 22L8 29L4 29Z\"/>\n" +
//                "<path fill=\"#66cc6d\" d=\"M10 10L15 10L15 15L10 15ZM20 10L20 15L15 15L15 10ZM20 20L15 20L15 15L20 15ZM10 20L10 15L15 15L15 20Z\"/>\n" +
//                "<path fill=\"#32993a\" d=\"M15 1L15 8L8 8ZM22 8L15 8L15 1ZM15 29L15 22L22 22ZM8 22L15 22L15 29ZM8 8L8 15L1 15ZM29 15L22 15L22 8ZM22 22L22 15L29 15ZM1 15L8 15L8 22Z\"/>\n" +
//                "</svg>"
////
//        override val root = vbox {
//            val svgLoader = SvgLoader()
//            val svgGroup = svgLoader.loadSvg(Jdenticon.toSvg(hash = "128243567654", size = 100).byteInputStream())
//            val svgButton = button {
//                graphic = svgGroup
//                prefWidth = 200.0
//                prefHeight = 200.0
//                resizeSvg(svgGroup, prefWidth, prefHeight)
//            }
//        }
//
//        init {
//            title = "TornadoFX Playground"
//            setWindowMinSize(480,320)
//        }
//        fun resizeSvg(svgGroup: Group, width: Double, height: Double) {
//            // adapted from https://stackoverflow.com/questions/38953921/how-to-set-the-size-of-a-svgpath-in-javafx
//            val currentWidth = svgGroup.prefWidth(-1.0) // get the default preferred width
//            val currentHeight = svgGroup.prefHeight(currentWidth) // get default preferred height
//
//            svgGroup.scaleX = width / currentWidth
//            svgGroup.scaleY = height / currentHeight
//        }
//    }
    override val root = vbox {

        stackpane {
            //            displays outer circle(grey color)behind the icon
            circle {
                radius = svgSize * 1.1
                fill = Color.LIGHTGRAY
            }
            button {
                            addClass("profileIcon")
                            stackpane {
                                //                displays inner circle(white color behind the Jdenticon)
                                circle {
                                    radius = svgSize * .70
                                    fill = Color.WHITE
                                    effect = DropShadow(10.0, Color.GRAY)
                                }
//                displays svg on diagonal directions
                                svgicon(svgString1, size = svgSize, color = Color.web("#b2e5b6", svgOpacity))
//                displays svg in the middle
//                second svgicon is 35% the size of the other two
                                svgicon(svgString2, size = svgSize * 0.35, color = Color.web("#66cc6d", svgOpacity))

//                displays SVG on the top, bottom, left, right
                                svgicon(svgString3, size = svgSize, color = Color.web("#32993a", svgOpacity))
                                onMouseClicked = EventHandler {
                                    println("inner circle click")
                    }
                }
            }

        }
    }
}
//            }
//        C:\Users\fucat\Documents\repositories\8woc2018-jvm\src\main\resources\10994433_10201216808058383_4904231688990374580_o.jpg
//            imageview("10994433_10201216808058383_4904231688990374580_o.jpg")
//        var a = Jdenticon.toSvg(hash = "12875865435", size = 100)
//        imageview(Jdenticon.toSvg(hash = "12875865435", size = 100))
//            onMouseClicked = EventHandler {
//                print1()
//            }
//            padding = Insets(300.0)
//            }
//        }
//    }
//
////var a = "println(Jdenticon.toSvg(hash = "12875865435", size = 100)) will give :<svg xmlns="http://www.w3.org/2000/svg" width="30" height="30" viewBox="0 0 30 30" preserveAspectRatio="xMidYMid meet">
////<path fill="#b2e5b6" d="M8 8L1 8L1 4ZM22 8L22 1L25 1ZM22 22L29 22L29 25ZM8 22L8 29L4 29Z"/>
////<path fill="#66cc6d" d="M10 10L15 10L15 15L10 15ZM20 10L20 15L15 15L15 10ZM20 20L15 20L15 15L20 15ZM10 20L10 15L15 15L15 20Z"/>
////<path fill="#32993a" d="M15 1L15 8L8 8ZM22 8L15 8L15 1ZM15 29L15 22L22 22ZM8 22L15 22L15 29ZM8 8L8 15L1 15ZM29 15L22 15L22 8ZM22 22L22 15L29 15ZM1 15L8 15L8 22Z"/>
////</svg>"
//
////fun parser(svgs: String): String {
////
////}
//
//fun print1() {
//    println("do button action")
//}

//fun parseSvg() {
//    val test = Jdenticon.toSvg(hash = "12875865435", size = 100).toString()
//
//
//    val list = emptyList<String>().toMutableList()
//
//    var temp = ""
//    var read = false
//    for (i in 0 until (test.length - 1)) {
//
//        if (!read) {
//            if (i - 2 >= 0 && test.substring(i - 2..i) == "d=\"") {
//                read = true
//            }
//        } else {
//            if (test[i] != '\"') {
//                temp += test[i]
//            } else {
//                read = false
//                list.add(temp)
//                temp = ""
//            }
//        }
//
//    }
//
//    for ((index, string) in list.withIndex()) {
//        svgString1 = string
//    }
//}