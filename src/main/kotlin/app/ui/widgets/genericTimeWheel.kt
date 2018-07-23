package app.ui.widgets

import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.ArcType
import javafx.util.Duration
import tornadofx.*
import tornadofx.Stylesheet.Companion.root

class genericTimeWheel(outerColor: Color, innerColor: Color, arcColor: Color): Pane() {

    val bigCircle = circle {
        centerX = 100.0
        centerY = 100.0
        radius = 100.0;
        fill = outerColor;
    }

    val mediumCircle = circle {
        centerX = 100.0
        centerY = 100.0
        radius = 90.0;
        fill = innerColor;
    }

    val arc = arc {
        fill = arcColor;
        centerX = 100.0
        centerY = 100.0
        radiusX = 91.0
        radiusY = 91.0
        startAngle = 0.0
        type = ArcType.ROUND
    }

    fun animate(timeMillis: Double) {
        timeline{
            keyframe(Duration.millis(timeMillis)) {
                keyvalue(arc.lengthProperty(), 360.0)
            }
        }
    }

    init{
        with(root) {
         //css stuff i think
        }
    }
}