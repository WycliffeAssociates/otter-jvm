package org.wycliffeassociates.otter.jvm.app.ui.debugbrowser.view

import com.jfoenix.controls.JFXButton
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Content
import org.wycliffeassociates.otter.jvm.app.images.ImageLoader
import org.wycliffeassociates.otter.jvm.app.images.SVGImage
import org.wycliffeassociates.otter.jvm.app.theme.AppStyles
import org.wycliffeassociates.otter.jvm.app.ui.debugbrowser.viewmodel.DebugBrowserViewModel
import org.wycliffeassociates.otter.jvm.app.widgets.projectcard.projectcard
import tornadofx.*

class DebugBrowserView : View() {

    private val viewModel: DebugBrowserViewModel by inject()
    private val noProjectsProperty: ReadOnlyBooleanProperty

    init {
        importStylesheet<DebugBrowserStyles>()
        // Setup property bindings to bind to empty property
        // https://stackoverflow.com/questions/21612969/is-it-possible-to-bind-the-non-empty-state-of-
        // an-observablelist-inside-an-object
        val listProperty = SimpleListProperty<Pair<Collection?, Content?>>()
        listProperty.bind(SimpleObjectProperty(viewModel.collectionsAndContents))
        noProjectsProperty = listProperty.emptyProperty()
    }

    override val root = anchorpane {
        addClass(AppStyles.appBackground)
        addClass(DebugBrowserStyles.homeAnchorPane)
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
                addClass(AppStyles.appBackground)
                addClass(DebugBrowserStyles.projectsFlowPane)
                bindChildren(viewModel.collectionsAndContents) {
                    val (collection, content) = it
                    collection?.let {
                        hbox {
                            projectcard(it) {
                                addClass(DebugBrowserStyles.projectCard)
                                titleLabel.addClass(DebugBrowserStyles.projectCardTitle)
                                languageLabel.addClass(DebugBrowserStyles.projectCardLanguage)
                                cardButton.apply {
                                    text = messages["loadProject"]
                                    action {
                                        viewModel.openCollection(it)
                                    }
                                }
                                graphicContainer.apply {
                                    addClass(DebugBrowserStyles.projectGraphicContainer)
                                    add(MaterialIconView(MaterialIcon.IMAGE, "75px"))
                                }
                            }
                        }
                    }
                            ?: vbox {
                                content?.let { label("${content.labelKey}${content.start} ${content.text ?: ""}") { } }
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
                addClass(DebugBrowserStyles.noProjectsLabel)
            }

            visibleProperty().bind(noProjectsProperty)
            managedProperty().bind(visibleProperty())
        }

        add(JFXButton("", MaterialIconView(MaterialIcon.ARROW_BACK, "25px")).apply {
            addClass(DebugBrowserStyles.backButton)
            isDisableVisualFocus = true
            anchorpaneConstraints {
                bottomAnchor = 25
                rightAnchor = 25
            }
            action { viewModel.goBack() }
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
}