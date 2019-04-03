package org.wycliffeassociates.otter.jvm.resourcestestapp.view

import com.jfoenix.controls.JFXCheckBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import org.wycliffeassociates.otter.jvm.resourcestestapp.styles.ProgressBannerStyles
import tornadofx.*

class ProgressBanner : VBox() {
    init {
        importStylesheet<ProgressBannerStyles>()

        addClass(ProgressBannerStyles.progressBanner)
        spacing = 10.0
        hbox {
            label("Translation Notes") {
//                graphic = AppStyles.tNGraphic()
            }
            region {
                hgrow = Priority.ALWAYS
            }
            add(
                JFXCheckBox("Hide Completed").apply {
                    isDisableVisualFocus = true
                }
            )
        }
        // TODO: Status bar
        region {
            hgrow = Priority.ALWAYS
            style {
                backgroundColor += Color.ORANGE
                prefHeight = 8.px
            }
        }
    }
}

fun progressbanner(init: ProgressBanner.() -> Unit = {}): ProgressBanner {
    val pb = ProgressBanner()
    pb.init()
    return pb
}