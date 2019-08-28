package org.wycliffeassociates.otter.jvm.app.ui.resources.viewmodel

import org.wycliffeassociates.otter.common.data.workbook.*
import org.wycliffeassociates.otter.common.navigation.TabGroupType
import org.wycliffeassociates.otter.common.utils.mapNotNull
import org.wycliffeassociates.otter.jvm.app.ui.chromeablestage.view.ChromeableStage
import org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.viewmodel.ResourceTabPaneViewModel
import org.wycliffeassociates.otter.jvm.app.ui.workbook.viewmodel.WorkbookViewModel
import org.wycliffeassociates.otter.jvm.app.widgets.resourcecard.model.ResourceGroupCardItemList
import org.wycliffeassociates.otter.jvm.app.widgets.resourcecard.model.resourceGroupCardItem
import org.wycliffeassociates.otter.jvm.utils.observeOnFxSafe
import org.wycliffeassociates.otter.jvm.utils.onChangeAndDoNow
import tornadofx.*

class ResourceListViewModel : ViewModel() {
    internal val resourceTabPaneViewModel: ResourceTabPaneViewModel by inject()
    private val workbookViewModel: WorkbookViewModel by inject()
    private val navigator: ChromeableStage by inject()

    val resourceGroupCardItemList: ResourceGroupCardItemList = ResourceGroupCardItemList()

    init {
        workbookViewModel.activeChapterProperty.onChangeAndDoNow { targetChapter ->
            targetChapter?.let {
                loadResourceGroups(getSourceChapter(targetChapter))
            }
        }
    }

    private fun getSourceChapter(targetChapter: Chapter): Chapter {
        return workbookViewModel.workbook.source.chapters.filter {
            it.title == targetChapter.title
        }.blockingFirst()
    }

    internal fun loadResourceGroups(chapter: Chapter) {
        chapter
            .children
            .startWith(chapter)
            .mapNotNull {
                resourceGroupCardItem(
                    it,
                    workbookViewModel.activeResourceInfo.slug,
                    onSelect = this::navigateToTakesPage
                )
            }
            .buffer(2) // Buffering by 2 prevents the list UI from jumping while groups are loading
            .observeOnFxSafe()
            .subscribe {
                resourceGroupCardItemList.addAll(it)
            }
    }

    private fun navigateToTakesPage(bookElement: BookElement, resource: Resource) {
        setActiveChunkAndRecordables(bookElement, resource)
        navigator.navigateTo(TabGroupType.RESOURCE_COMPONENT)
    }

    internal fun setActiveChunkAndRecordables(bookElement: BookElement, resource: Resource) {
        workbookViewModel.activeChunkProperty.set(bookElement as? Chunk)
        resourceTabPaneViewModel.setRecordableListItems(
            listOfNotNull(resource.title, resource.body)
        )
    }
}