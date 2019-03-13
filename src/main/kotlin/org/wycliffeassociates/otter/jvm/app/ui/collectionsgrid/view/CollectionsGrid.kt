package org.wycliffeassociates.otter.jvm.app.ui.collectionsgrid.view

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.property.Property
import javafx.event.EventHandler
import javafx.scene.control.TabPane
import javafx.scene.layout.Priority
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.ResourceMetadata
import org.wycliffeassociates.otter.jvm.app.theme.AppStyles
import org.wycliffeassociates.otter.jvm.app.ui.collectionsgrid.viewmodel.CollectionsGridViewModel
import org.wycliffeassociates.otter.jvm.app.ui.mainscreen.view.MainScreenStyles
import org.wycliffeassociates.otter.jvm.app.widgets.card.DefaultStyles
import org.wycliffeassociates.otter.jvm.app.widgets.card.card
import tornadofx.*

class CollectionsGrid : Fragment() {
    private val viewModel: CollectionsGridViewModel by inject()

    val activeProject: Property<Collection> = viewModel.activeProjectProperty
    val activeCollection: Property<Collection> = viewModel.activeCollectionProperty
    val activeResource: Property<ResourceMetadata> = viewModel.activeResourceProperty

    init {
        importStylesheet<CollectionGridStyles>()
        importStylesheet<DefaultStyles>()
    }

    override val root = anchorpane {
        vbox {
            anchorpaneConstraints {
                topAnchor = 0
                rightAnchor = 0
                bottomAnchor = 0
                leftAnchor = 0
            }
            collectionTabPane(viewModel.resourceList, activeResource) {
                tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
                prefHeight = MainScreenStyles.menuBarHeight.value
                // 9 is emperical number to subtract, + 2 for top border width
                tabMinHeightProperty().bind(prefHeightProperty().subtract(11))
            }
            progressindicator {
                visibleProperty().bind(viewModel.loadingProperty)
                managedProperty().bind(visibleProperty())
                addClass(CollectionGridStyles.contentLoadingProgress)
            }
            datagrid(viewModel.children) {
                vgrow = Priority.ALWAYS
                hgrow = Priority.ALWAYS
                isFillWidth = true
                addClass(AppStyles.workingArea)
                addClass(CollectionGridStyles.collectionsContainer)
                cellCache { item ->
                    card {
                        addClass(DefaultStyles.defaultCard)
                        cardfront {
                            innercard(AppStyles.chapterGraphic()) {
                                title = item.labelKey.toUpperCase()
                                bodyText = item.titleKey
                            }
                            cardbutton {
                                addClass(DefaultStyles.defaultCardButton)
                                bindClass(viewModel.selectedResourceClassProperty)
                                text = messages["openProject"]
                                graphic = MaterialIconView(MaterialIcon.ARROW_FORWARD, "25px")
                                        .addClass(DefaultStyles.defaultCardButtonIcon)
                                isDisableVisualFocus = true
                                onMousePressed = EventHandler {
                                    viewModel.selectCollection(item)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}