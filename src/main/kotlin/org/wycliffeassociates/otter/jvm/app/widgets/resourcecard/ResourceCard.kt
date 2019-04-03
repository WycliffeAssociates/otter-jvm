package org.wycliffeassociates.otter.jvm.app.widgets.resourcecard

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import org.wycliffeassociates.otter.common.data.rxmodel.Resource
import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
import org.wycliffeassociates.otter.jvm.app.widgets.highlightablebutton.HighlightableButton
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
                vbox {
                    prefHeight = 5.0
                    prefWidth = 75.0
                    style {
                        backgroundColor += Color.GREEN
                    }
                }
                vbox {
                    prefHeight = 5.0
                    prefWidth = 75.0
                    style {
                        backgroundColor += Color.BLUE
                    }
                }
            }
            text(resource.title.text)
            maxWidth = 150.0
        }

        region {
            hgrow = Priority.ALWAYS
        }

        add(
            HighlightableButton(
                AppTheme.colors.appOrange,
                AppTheme.colors.white,
                false,
                isCurrentResource,
                MaterialIconView(MaterialIcon.APPS, "25px")
            ).apply {
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