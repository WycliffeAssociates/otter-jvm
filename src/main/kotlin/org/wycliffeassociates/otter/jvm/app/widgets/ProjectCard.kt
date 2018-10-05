package org.wycliffeassociates.otter.jvm.app.widgets

import javafx.beans.property.SimpleStringProperty
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import org.wycliffeassociates.otter.common.data.model.ProjectCollection
import tornadofx.*
import tornadofx.Stylesheet.Companion.root

class ProjectCard(project: ProjectCollection) : VBox() {
    val loadButton = Button()
    var buttonTextProperty = SimpleStringProperty("")
    var buttonText by buttonTextProperty

    init {
        with(root) {
            vbox(20) {
                alignment = Pos.CENTER
                label(project.titleKey)
                label(project.labelKey)
                button(buttonTextProperty) {

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
