package org.wycliffeassociates.otter.jvm.app.ui.viewcontent.view

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.property.Property
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.stage.Screen
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Content
import org.wycliffeassociates.otter.jvm.app.images.ImageLoader
import org.wycliffeassociates.otter.jvm.app.theme.AppStyles
import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
import org.wycliffeassociates.otter.jvm.app.ui.viewcontent.ViewContentStyles
import org.wycliffeassociates.otter.jvm.app.ui.viewcontent.viewmodel.ViewContentViewModel
import org.wycliffeassociates.otter.jvm.app.widgets.card.DefaultStyles
import org.wycliffeassociates.otter.jvm.app.widgets.card.card
import tornadofx.*


class ViewContent : View() {
    private val viewModel: ViewContentViewModel by inject()

    val activeCollection: Property<Collection> = viewModel.activeCollectionProperty
    val activeProject: Property<Collection> = viewModel.activeProjectProperty
    val activeContent: Property<Content> = viewModel.activeContentProperty

    init {
        importStylesheet<ViewContentStyles>()
        importStylesheet<DefaultStyles>()
    }

    override val root = stackpane {
        hgrow = Priority.ALWAYS
        vgrow = Priority.ALWAYS
        style {
            prefWidth = Screen.getPrimary().visualBounds.width.px - 50.0
            prefHeight = Screen.getPrimary().visualBounds.height.px - 50.0
        }
        addClass(AppStyles.appBackground)
        hbox {

            vbox {
                hgrow = Priority.ALWAYS
                vbox {
                    vgrow = Priority.ALWAYS
                    progressindicator {
                        visibleProperty().bind(viewModel.loadingProperty)
                        managedProperty().bind(visibleProperty())
                        addClass(ViewContentStyles.contentLoadingProgress)
                    }
                    scrollpane {
                        isFitToHeight = true
                        isFitToWidth = true
                        flowpane {
                            addClass(AppStyles.appBackground)
                            addClass(ViewContentStyles.collectionsFlowpane)
                            bindChildren(viewModel.filteredContent) {
                                vbox {
                                    add(card {
                                        addClass(DefaultStyles.defaultCard)
                                        cardfront {
                                            var cardGraphic = ImageLoader.load(
                                                    ClassLoader.getSystemResourceAsStream("images/verse_image.png"),
                                                    ImageLoader.Format.PNG
                                            )
                                            innercard(cardGraphic) {
                                                title = it.first.value.labelKey.toUpperCase()
                                                bodyText = it.first.value.start.toString()
                                                style {
                                                    maxHeight = 118.px
                                                    maxWidth = 142.px
                                                    backgroundColor += AppTheme.colors.lightBackground
                                                    borderColor += box(Color.WHITE)
                                                    borderWidth += box(3.0.px)
                                                    borderRadius += box(5.0.px)
                                                    borderInsets += box(1.5.px)
                                                }
                                            }
                                            cardbutton {
                                                addClass(DefaultStyles.defaultCardButton)
                                                text = messages["openProject"]
                                                graphic = MaterialIconView(MaterialIcon.ARROW_FORWARD, "25px")
                                                        .apply { fill = AppTheme.colors.appRed }
                                                action {
                                                    viewModel.viewContentTakes(it.first.value)
                                                }
                                            }
                                        }

                                    })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}