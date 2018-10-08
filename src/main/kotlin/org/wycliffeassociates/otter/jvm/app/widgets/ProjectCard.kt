package org.wycliffeassociates.otter.jvm.app.widgets

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import org.wycliffeassociates.otter.common.data.model.ProjectCollection
import org.wycliffeassociates.otter.jvm.app.ui.imageLoader
import tornadofx.*
import tornadofx.Stylesheet.Companion.root
import kotlin.math.max

class ProjectCard(project: ProjectCollection) : VBox() {
    val loadButton = Button()
    var buttonTextProperty = SimpleStringProperty("")
    var buttonText by buttonTextProperty

    init {
        with(root) {
            alignment = Pos.BOTTOM_CENTER
            vbox(20) {
                alignment= Pos.CENTER
                label(project.titleKey) {
                    textAlignment = TextAlignment.CENTER
                }
                label(project.labelKey)  {
                    textAlignment = TextAlignment.CENTER
                }
                button(buttonTextProperty) {
                    style {
                        prefWidth =232.0.px
                        prefHeight = 40.0.px
                    }
                }
            }
        }
    }
}

fun Pane.projectcard(project: ProjectCollection, init: ProjectCard.() -> Unit = {}): ProjectCard {
    val projectCard = ProjectCard(project)
    projectCard.init()
    add(projectCard)
    return projectCard
}
