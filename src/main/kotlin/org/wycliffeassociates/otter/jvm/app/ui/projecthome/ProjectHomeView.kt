package org.wycliffeassociates.otter.jvm.app.ui.projecthome

import com.jfoenix.controls.JFXButton
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.jvm.app.ui.imageLoader
import org.wycliffeassociates.otter.jvm.app.ui.styles.AppStyles
import org.wycliffeassociates.otter.jvm.app.widgets.projectcard
import tornadofx.*
import java.io.File

class ProjectHomeView : View() {

    val viewModel: ProjectHomeViewModel by inject()
    val noProjectsProperty: ReadOnlyBooleanProperty

    init {
        importStylesheet<AppStyles>()
        // Setup property bindings to bind to empty property
        // https://stackoverflow.com/questions/21612969/is-it-possible-to-bind-the-non-empty-state-of-
        // an-observablelist-inside-an-object
        val listProperty = SimpleListProperty<Collection>()
        listProperty.bind(SimpleObjectProperty(viewModel.allProjects))
        noProjectsProperty = listProperty.emptyProperty()
    }

    override val root = anchorpane {
        style {
            setPrefSize(1200.0, 800.0)
        }
        scrollpane {
            isFitToHeight = true
            isFitToWidth = true
            anchorpaneConstraints {
                topAnchor = 0
                bottomAnchor = 0
                leftAnchor = 0
                rightAnchor = 0
            }
            content = flowpane {
                vgap = 16.0
                hgap = 16.0
                alignment = Pos.TOP_LEFT
                // Add larger padding on bottom to keep FAB from blocking last row cards
                padding = Insets(10.0, 10.0, 95.0, 10.0)
                bindChildren(viewModel.allProjects) {
                    hbox {
                        projectcard(it) {
                            addClass(AppStyles.projectCard)
                            titleLabel.addClass(AppStyles.projectCardTitle)
                            languageLabel.addClass(AppStyles.projectCardLanguage)
                            cardButton.apply {
                                text = messages["loadProject"]
                                action {
                                    viewModel.openProject(it)
                                }
                            }
                            graphicContainer.apply {
                                addClass(AppStyles.projectGraphicContainer)
                                add(MaterialIconView(MaterialIcon.IMAGE, "75px"))
                            }
                        }
                    }
                }
            }
        }

        vbox {
            vbox {
                alignment = Pos.BOTTOM_CENTER
                vgrow = Priority.ALWAYS
                label(messages["noProjects"]) {
                    addClass(AppStyles.noProjectsLabel)
                }
                label(messages["noProjectsSubtitle"]) {
                    addClass(AppStyles.tryCreatingLabel)
                }
            }
            vbox {
                alignment = Pos.BOTTOM_RIGHT
                vgrow = Priority.ALWAYS
                style {
                    padding = box(0.px, 100.px, 50.px, 0.px)
                }
                add(imageLoader(
                        File(
                                ClassLoader
                                        .getSystemResource("assets${File.separator}project_home_arrow.svg")
                                        .toURI()
                        )
                ).apply {
                    style {
                        maxWidth = 400.px
                        maxHeight = 229.px
                        minWidth = 400.px
                        minHeight = 229.px
                    }
                })

            }

            visibleProperty().bind(noProjectsProperty)
            managedProperty().bind(visibleProperty())
        }
        add(JFXButton("", MaterialIconView(MaterialIcon.ADD, "25px")).apply {
            addClass(AppStyles.addProjectButton)
            anchorpaneConstraints {
                bottomAnchor = 25
                rightAnchor = 25
            }
            action {
                viewModel.createProject()
            }
        })

    }

    override fun onDock() {
        viewModel.getAllProjects()
    }

}