package org.wycliffeassociates.otter.jvm.app.widgets.progressstepper

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.paint.Color
import org.wycliffeassociates.otter.jvm.app.theme.AppStyles
import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
import tornadofx.*

class ProgressStepper : Fragment() {
    val stepNumberProperty = SimpleIntegerProperty(3)
    var stepNumber by stepNumberProperty

    val totalStepsProperty = SimpleIntegerProperty(-1)
    var totalSteps by totalStepsProperty

    val fillColorProperty = SimpleObjectProperty<Color>()
    var fillColor by fillColorProperty

    override val root = hbox(5.0) {
        alignment = Pos.CENTER
        for (x in 1..stepNumberProperty.value) {
            circle {
                radius = 10.0
                centerX = x * 10.0
                centerY = 100.0
                fill = AppTheme.colors.appRed
            }
            if (x != stepNumberProperty.value) {
                rectangle {
                    width = 100.0
                    height = 2.5
                    arcWidth = 5.0
                    arcHeight = 5.0
                    fill = AppTheme.colors.appRed

                }
            } else {
                rectangle {
                    width = 100.0
                    height = 2.5
                    arcWidth = 5.0
                    arcHeight = 5.0
                    fill = Color.WHITE

                }
            }
        }
    }


    fun draw() {
    }

}