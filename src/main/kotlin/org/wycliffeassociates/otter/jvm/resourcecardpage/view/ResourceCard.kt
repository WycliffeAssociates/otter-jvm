package org.wycliffeassociates.otter.jvm.resourcecardpage.view

import javafx.geometry.Pos
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import org.wycliffeassociates.otter.common.data.rxmodel.Resource
import org.wycliffeassociates.otter.jvm.resourcecardpage.styles.ResourceCardStyles
import org.wycliffeassociates.otter.jvm.resourcecardpage.viewmodel.ResourceCardViewModel
import org.wycliffeassociates.otter.jvm.resourcecardpage.widgets.ViewRecordingsButton
import tornadofx.*

class ResourceCard(private val resource: Resource) : HBox() {

    val viewModel = ResourceCardViewModel(resource)

    // TODO: Title progress boolean property
    // TODO: Body progress boolean property
    // TODO: Is last worked on boolean property (for recording button highlighted)
    // TODO: ????? text  (doesn't need to be a property?)
    init {
        importStylesheet<ResourceCardStyles>()

        isFillHeight = false
        alignment = Pos.CENTER_LEFT
        maxHeight = 50.0

        vbox {
            spacing = 3.0
            hbox {
                spacing = 3.0
                // TODO: Replace these with status indicators
                vbox {
                    prefHeight = 5.0
                    prefWidth = 75.0
                    style {
                        backgroundColor += Color.GREEN
                    }
                    visibleProperty().bind(viewModel.titleTakeSelected)
                }
                vbox {
                    prefHeight = 5.0
                    prefWidth = 75.0
                    style {
                        backgroundColor += Color.BLUE
                    }
                    visibleProperty().bind(viewModel.bodyTakeSelected)
                }
            }
            text(resource.title.text)
            maxWidth = 150.0
        }

        region {
            hgrow = Priority.ALWAYS
        }

        add(
                ViewRecordingsButton().apply {
                    action {
                        viewModel.navigateToTakesPage()
                    }
                }
        )
    }
}

fun resourcecard(resource: Resource, init: ResourceCard.() -> Unit = {}): ResourceCard {
    val rc = ResourceCard(resource)
    rc.init()
    return rc
}