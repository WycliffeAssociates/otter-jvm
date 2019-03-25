package org.wycliffeassociates.otter.jvm.resourcecardpage.view

import com.jfoenix.controls.JFXButton
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Pos
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import org.wycliffeassociates.otter.common.data.model.Resource
import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
import org.wycliffeassociates.otter.jvm.app.widgets.card.DefaultStyles
import tornadofx.*
import tornadofx.FX.Companion.messages

class ResourceCard(private val resource: Resource) : HBox() {

    // TODO: Title progress boolean property
    // TODO: Body progress boolean property
    // TODO: Is last worked on boolean property (for recording button highlighted)
    // TODO: ????? text  (doesn't need to be a property?)

    init {
        importStylesheet<DefaultStyles>()

        paddingLeft = 10.0
        paddingTop = 10.0
        paddingRight = 10.0
        paddingBottom = 10.0

        isFillHeight = false

        alignment = Pos.CENTER_LEFT
//        alignment = Pos.TOP_CENTER
        maxHeight = 50.0

        style {
            backgroundColor += Color.PURPLE
        }

        vbox {
            style {
                backgroundColor += Color.YELLOW
            }
            alignment = Pos.CENTER_LEFT // TODO
            hbox {
                spacing = 3.0
                vbox {
                    prefHeight = 5.0
                    prefWidth = 80.0
                    style {
                        backgroundColor += Color.GREEN
                    }
                }
                vbox {
                    prefHeight = 5.0
                    prefWidth = 80.0
                    style {
                        backgroundColor += Color.BLUE
                    }
                }
            }
            text(resource.title.text) {
//                alignment = Pos.CENTER_LEFT // TODO
            }
            maxWidth = 100.0
        }

        region {
            hgrow = Priority.ALWAYS
        }

        add(
                JFXButton().apply {
//                    alignment = Pos.CENTER_RIGHT
                    addClass(DefaultStyles.defaultCardButton)
                    text = messages["openProject"]
                    graphic = MaterialIconView(MaterialIcon.GRID_ON, "25px")
                            .apply { fill = AppTheme.colors.appRed }
                }
        )
    }
}

fun resourcecard(resource: Resource, init: ResourceCard.() -> Unit = {}): ResourceCard {
    val rc = ResourceCard(resource)
    rc.init()
    return rc
}