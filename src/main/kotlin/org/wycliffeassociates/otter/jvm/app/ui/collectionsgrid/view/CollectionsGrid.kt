package org.wycliffeassociates.otter.jvm.app.ui.collectionsgrid.view

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.application.Platform
import javafx.beans.property.Property
import javafx.beans.property.ReadOnlyDoubleProperty
import javafx.event.EventHandler
import javafx.scene.layout.Priority
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.jvm.app.theme.AppStyles
import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
import org.wycliffeassociates.otter.jvm.app.ui.collectionsgrid.viewmodel.CollectionsGridViewModel
import org.wycliffeassociates.otter.jvm.app.widgets.card.DefaultStyles
import org.wycliffeassociates.otter.jvm.app.widgets.card.card
import tornadofx.*

class CollectionsGrid : Fragment() {
    private val viewModel: CollectionsGridViewModel by inject()

    val activeCollection: Property<Collection> = viewModel.activeCollectionProperty
    val activeProject: Property<Collection> = viewModel.activeProjectProperty

    init {
        importStylesheet<CollectionGridStyles>()
        importStylesheet<DefaultStyles>()
    }

    override val root = vbox {
        addClass(AppStyles.appBackground)
        hgrow = Priority.ALWAYS
        vgrow = Priority.ALWAYS
        progressindicator {
            visibleProperty().bind(viewModel.loadingProperty)
            managedProperty().bind(visibleProperty())
            addClass(CollectionGridStyles.contentLoadingProgress)
        }

        datagrid(viewModel.allContent) {
            vgrow = Priority.ALWAYS
            hgrow = Priority.ALWAYS
            isFillWidth = true
            addClass(AppStyles.appBackground)
            addClass(CollectionGridStyles.collectionsContainer)
            cellCache { item ->
                card {
                    addClass(DefaultStyles.defaultCard)
                    cardfront {
                        isCompleteProperty.bind(item.second.booleanBinding{it == 1.0})
                        innercard(AppStyles.chapterGraphic()) {
                            title = item.first.value.labelKey.toUpperCase()
                            bodyText = item.first.value.titleKey
                            showProgress = true
                            progressProperty.bind(item.second)
                        }
                        cardbutton {
                            addClass(DefaultStyles.defaultCardButton)
                            text = messages["openProject"]
                            graphic = MaterialIconView(MaterialIcon.ARROW_FORWARD, "25px")
                                    .apply { fill = AppTheme.colors.appRed }
                            isDisableVisualFocus = true
                            onMousePressed = EventHandler {
                                viewModel.selectCollection(item.first.value)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDock() {
        super.onDock()
        viewModel.refresh()
    }

    override fun onUndock() {
        super.onUndock()
    }
}