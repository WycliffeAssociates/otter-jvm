package org.wycliffeassociates.otter.jvm.app.ui.helpcontentlist.view

import javafx.beans.property.Property
import javafx.scene.Node
import javafx.scene.layout.Priority
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Content
import org.wycliffeassociates.otter.common.data.model.ResourceMetadata
import org.wycliffeassociates.otter.jvm.app.theme.AppStyles
import org.wycliffeassociates.otter.jvm.app.ui.helpcontentlist.viewmodel.GroupedContents
import org.wycliffeassociates.otter.jvm.app.ui.helpcontentlist.viewmodel.HelpContentListViewModel
import org.wycliffeassociates.otter.jvm.app.ui.mainscreen.view.MainScreenStyles
import org.wycliffeassociates.otter.jvm.app.widgets.card.DefaultStyles
import tornadofx.*

class HelpContentList : Fragment() {
    private val viewModel: HelpContentListViewModel by inject()

    val activeProject: Property<Collection> = viewModel.activeProjectProperty
    val activeCollection: Property<Collection> = viewModel.activeCollectionProperty
    val activeResource: Property<ResourceMetadata> = viewModel.activeResourceProperty
    val activeContent: Property<Content> = viewModel.activeContentProperty

    init {
        importStylesheet<HelpContentListStyles>()
        importStylesheet<DefaultStyles>()
        importStylesheet<MainScreenStyles>()
    }

    private fun buildNote(items: GroupedContents): Node {
        return vbox {
            isFillWidth = true
            label("Verse ${items.label.value}") { }
            items.contentInfos.value.forEach {
                val displayContent = it.source.value ?: it.derived.value
                displayContent.run {
                    label("$sort ${text ?: "(no text)"}") {
                        toggleClass(HelpContentListStyles.helpTitle, labelKey == "title")
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
            addClass(HelpContentListStyles.contentLoadingProgress)
        }

        listview(viewModel.filteredContents) {
            vgrow = Priority.ALWAYS
            hgrow = Priority.ALWAYS
            isFillWidth = true
            addClass(AppStyles.appBackground)
            addClass(HelpContentListStyles.helpContentContainer)
            cellCache { buildNote(it) }
        }
    }
}
