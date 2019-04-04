package org.wycliffeassociates.otter.jvm.app.widgets.resourcecard

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import org.wycliffeassociates.otter.common.data.workbook.Resource
import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
import org.wycliffeassociates.otter.jvm.app.widgets.highlightablebutton.highlightablebutton
import org.wycliffeassociates.otter.jvm.statusindicator.control.StatusIndicator
import org.wycliffeassociates.otter.jvm.statusindicator.control.statusindicator
import tornadofx.*
import tornadofx.FX.Companion.messages

class ResourceCard(private val resource: Resource) : HBox() {

    val isCurrentResource = SimpleBooleanProperty(false)

    init {
        isFillHeight = false
        alignment = Pos.CENTER_LEFT
        maxHeight = 50.0

        vbox {
            spacing = 3.0
            hbox {
                spacing = 3.0
                add(
                    statusindicator {
                        initForResourceCard()
                        progress = 1.0
                    }
                )
                add(
                    statusindicator {
                        initForResourceCard()
                        progress = 0.0
                    }
                )
            }
            text(resource.title.text)
            maxWidth = 150.0
        }

        region {
            hgrow = Priority.ALWAYS
        }

        add(
            highlightablebutton {
                primaryColor = AppTheme.colors.appOrange
                secondaryColor = AppTheme.colors.white
                isHighlighted = isCurrentResource
                graphic = MaterialIconView(MaterialIcon.APPS, "25px")
                maxWidth = 500.0
                text = messages["viewRecordings"]
            }
        )
    }
}

fun resourcecard(resource: Resource, init: ResourceCard.() -> Unit = {}): ResourceCard {
    val rc = ResourceCard(resource)
    rc.init()
    return rc
}

private fun StatusIndicator.initForResourceCard() {
    prefHeight = 5.0
    prefWidth = 75.0
    primaryFill = AppTheme.colors.appOrange
    accentFill = AppTheme.colors.lightBackground
    trackFill = AppTheme.colors.defaultBackground
    indicatorRadius = 3.0
}