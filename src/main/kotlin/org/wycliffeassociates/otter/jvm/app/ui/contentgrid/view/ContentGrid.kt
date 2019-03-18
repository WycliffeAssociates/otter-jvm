package org.wycliffeassociates.otter.jvm.app.ui.contentgrid.view

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.binding.Bindings.createBooleanBinding
import javafx.beans.binding.BooleanExpression
import javafx.beans.property.Property
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.layout.Priority
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Content
import org.wycliffeassociates.otter.common.data.model.ResourceMetadata
import org.wycliffeassociates.otter.jvm.app.theme.AppStyles
import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
import org.wycliffeassociates.otter.jvm.app.ui.contentgrid.viewmodel.ContentGridViewModel
import org.wycliffeassociates.otter.jvm.app.ui.contentgrid.viewmodel.ContentInfo
import org.wycliffeassociates.otter.jvm.app.ui.contentgrid.viewmodel.GroupedContents
import org.wycliffeassociates.otter.jvm.app.ui.mainscreen.view.MainScreenStyles
import org.wycliffeassociates.otter.jvm.app.widgets.card.Card
import org.wycliffeassociates.otter.jvm.app.widgets.card.DefaultStyles
import org.wycliffeassociates.otter.jvm.app.widgets.card.card
import tornadofx.*
import java.util.concurrent.Callable

class ContentGrid : Fragment() {
    private val viewModel: ContentGridViewModel by inject()

    val activeProject: Property<Collection> = viewModel.activeProjectProperty
    val activeCollection: Property<Collection> = viewModel.activeCollectionProperty
    val activeResource: Property<ResourceMetadata> = viewModel.activeResourceProperty
    val activeContent: Property<Content> = viewModel.activeContentProperty

    private val activeResourceIsHelp: BooleanExpression =
            createBooleanBinding(Callable { activeResource.value.type == "help" }, activeResource)

    init {
        importStylesheet<ContentGridStyles>()
        importStylesheet<DefaultStyles>()
        importStylesheet<MainScreenStyles>()
    }

    private fun buildContentWidget(contentsGroup: GroupedContents): Node {
        return when (activeResource.value?.type) {
            "help" -> buildNote(contentsGroup)
            else -> buildCard(contentsGroup.content.first())
        }
    }

    private fun buildNote(items: GroupedContents): Node {
        return vbox {
            isFillWidth = true
            label("Verse ${items.label.value}") { }
            items.content.value.forEach {
                it.content.value.run {
                    label("$labelKey ${text ?: "(no text)"}") { }
                }
            }
        }
    }

    private fun buildCard(item: ContentInfo): Card {
        return card {
            addClass(DefaultStyles.defaultCard)
            cardfront {
                innercard(AppStyles.chunkGraphic()) {
                    title = item.content.value.labelKey.toUpperCase()
                    bodyText = item.content.value.start.toString()
                }
                cardbutton {
                    addClass(DefaultStyles.defaultCardButton)
                    text = messages["openProject"]
                    graphic = MaterialIconView(MaterialIcon.ARROW_FORWARD, "25px")
                            .apply { fill = AppTheme.colors.appRed }
                    onMousePressed = EventHandler {
                        viewModel.viewContentTakes(item.content.value)
                    }
                }
            }
        }
    }

    override val root = vbox {
        hgrow = Priority.ALWAYS
        vgrow = Priority.ALWAYS
        addClass(AppStyles.workingArea)
        addClass(MainScreenStyles.belowMenuBar)
        progressindicator {
            visibleProperty().bind(viewModel.loadingProperty)
            managedProperty().bind(visibleProperty())
            addClass(ContentGridStyles.contentLoadingProgress)
        }

        datagrid(viewModel.filteredContents) {
            vgrow = Priority.ALWAYS
            hgrow = Priority.ALWAYS
            isFillWidth = true
            addClass(AppStyles.appBackground)
            toggleClass(ContentGridStyles.cardContentContainer, activeResourceIsHelp.not())
            toggleClass(ContentGridStyles.helpContentContainer, activeResourceIsHelp)
            cellCache { buildContentWidget(it) }
        }
    }
}
