package org.wycliffeassociates.otter.jvm.app.ui.projecthome.view

import com.jfoenix.controls.JFXButton
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.jvm.app.images.ImageLoader
import org.wycliffeassociates.otter.jvm.app.images.SVGImage
import org.wycliffeassociates.otter.jvm.app.theme.AppStyles
import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
import org.wycliffeassociates.otter.jvm.app.ui.projecthome.viewmodel.ProjectHomeViewModel
import org.wycliffeassociates.otter.jvm.app.widgets.card.DefaultStyles
import org.wycliffeassociates.otter.jvm.app.widgets.card.card
import tornadofx.*

class ProjectHomeView : Fragment() {

    private val viewModel: ProjectHomeViewModel by inject()
    private val noProjectsProperty: ReadOnlyBooleanProperty

    val activeProject: ReadOnlyObjectProperty<Collection> = viewModel.selectedProjectProperty

    init {
        importStylesheet<ProjectHomeStyles>()
        importStylesheet<DefaultStyles>()
        // Setup property bindings to bind to empty property
        // https://stackoverflow.com/questions/21612969/is-it-possible-to-bind-the-non-empty-state-of-
        // an-observablelist-inside-an-object
        val listProperty = SimpleListProperty<Collection>()
        listProperty.bind(SimpleObjectProperty(viewModel.projects))
        noProjectsProperty = listProperty.emptyProperty()
    }

    override val root = anchorpane {
        addClass(AppStyles.appBackground)
        addClass(ProjectHomeStyles.homeAnchorPane)
        scrollpane {
            isFitToHeight = true
            isFitToWidth = true
            anchorpaneConstraints {
                topAnchor = 0
                bottomAnchor = 0
                leftAnchor = 0
                rightAnchor = 0
            }
            content =
                    hbox {
                        addClass(AppStyles.appBackground)
                        flowpane {
                            hgrow = Priority.ALWAYS
                            addClass(AppStyles.appBackground)
                            addClass(ProjectHomeStyles.projectsFlowPane)
                            bindChildren(viewModel.projects) {
                                hbox {

                                    add(card {
                                        addClass(DefaultStyles.defaultCard)
                                        cardfront {
                                            isComplete = true
                                            innercard {
                                                majorLabel = it.titleKey
                                                minorLabel = it.resourceContainer?.language?.name
                                                style {
                                                    prefHeight = 118.px
                                                    prefWidth = 142.px
                                                }
                                            }
                                            cardbutton {
                                                addClass(DefaultStyles.defaultCardButton)
                                                text = messages["openProject"]
                                                graphic = MaterialIconView(MaterialIcon.ARROW_FORWARD, "25px")
                                                        .apply { fill = AppTheme.colors.appRed }
                                                action {
                                                    viewModel.openProject(it)
                                                }
                                            }
                                        }

                                    })
                                }
                            }
                        }
                    }
        }

        vbox {
            anchorpaneConstraints {
                topAnchor = 0
                leftAnchor = 0
                bottomAnchor = 0
                rightAnchor = 0
            }
            alignment = Pos.CENTER
            vgrow = Priority.ALWAYS
            label(messages["noProjects"]) {
                addClass(ProjectHomeStyles.noProjectsLabel)
            }
            label(messages["noProjectsSubtitle"]) {
                addClass(ProjectHomeStyles.tryCreatingLabel)
            }

            visibleProperty().bind(noProjectsProperty)
            managedProperty().bind(visibleProperty())
        }

        add(JFXButton("", MaterialIconView(MaterialIcon.ADD, "25px")).apply {
            addClass(ProjectHomeStyles.addProjectButton)
            isDisableVisualFocus = true
            anchorpaneConstraints {
                bottomAnchor = 25
                rightAnchor = 25
            }
            action { viewModel.createProject() }
        })
    }

    init {
        with(root) {
            add(ImageLoader.load(
                    ClassLoader.getSystemResourceAsStream("images/project_home_arrow.svg"),
                    ImageLoader.Format.SVG
            ).apply {
                if (this is SVGImage) preserveAspect = false
                root.widthProperty().onChange {
                    anchorpaneConstraints { leftAnchor = it / 2.0 }
                }
                root.heightProperty().onChange {
                    anchorpaneConstraints { topAnchor = it / 2.0 + 75.0 }
                }
                anchorpaneConstraints {
                    rightAnchor = 125
                    bottomAnchor = 60
                }

                visibleProperty().bind(noProjectsProperty)
                managedProperty().bind(visibleProperty())
            })
        }
    }

    override fun onDock() {
        viewModel.loadProjects()
        viewModel.clearSelectedProject()
    }
}