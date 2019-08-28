package org.wycliffeassociates.otter.jvm.app.ui.chromeablestage.tabgroups

import javafx.scene.control.Tab
import org.wycliffeassociates.otter.common.data.workbook.ResourceInfo
import org.wycliffeassociates.otter.jvm.app.ui.cardgrid.view.CardGridFragment
import org.wycliffeassociates.otter.jvm.utils.startWith
import tornadofx.*

class SelectChapterTabGroup : TabGroup() {
    private val tabMap: MutableMap<String, Tab> = mutableMapOf()

    override fun activate() {
        workbookViewModel.activeChapterProperty.set(null)
        val currentActiveResourceInfo = workbookViewModel.activeResourceInfoProperty.value

        createTabs()
        tabPane.tabs.addAll(tabMap.values)

        // Adding these tabs can change the active resource property so we need to
        // change it back to what it was originally
        if (currentActiveResourceInfo != null) {
            restoreActiveResourceInfo(currentActiveResourceInfo)
        }
    }

    private fun getTargetBookResourceInfo() =
        workbookViewModel.workbook.target.resourceContainer.let {
            ResourceInfo(
                slug = it.identifier,
                title = it.title,
                type = it.type
            )
        }

    private fun createTabs() {
        workbookViewModel.workbook.source.subtreeResources
            .startWith(getTargetBookResourceInfo())
            .map { info ->
                tabMap.putIfAbsent(info.slug, ChapterSelectTab(info))
            }
    }

    private fun restoreActiveResourceInfo(resourceInfo: ResourceInfo) {
        workbookViewModel.activeResourceInfoProperty.set(resourceInfo)
        tabMap[resourceInfo.slug]?.select()
    }

    private inner class ChapterSelectTab(val resourceInfo: ResourceInfo) : Tab() {
        init {
            text = resourceInfo.slug
            add(CardGridFragment().root)
            onSelected {
                workbookViewModel.activeResourceInfoProperty.set(resourceInfo)
            }
        }

        private fun onSelected(op: () -> Unit) {
            selectedProperty().onChange { selected ->
                if (selected) {
                    op()
                }
            }
        }
    }
}
