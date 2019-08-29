package org.wycliffeassociates.otter.jvm.app.ui.chromeablestage.tabgroups

import javafx.scene.control.Tab
import org.wycliffeassociates.otter.common.data.workbook.ResourceInfo
import org.wycliffeassociates.otter.jvm.app.ui.cardgrid.view.CardGridFragment
import org.wycliffeassociates.otter.jvm.app.ui.workbook.viewmodel.WorkbookViewModel
import tornadofx.*

class ChapterTabGroup : TabGroup() {
    private val workbookViewModel: WorkbookViewModel by inject()
    private val tabMap: MutableMap<String, Tab> = mutableMapOf()

    override fun activate() {
        workbookViewModel.activeChapterProperty.set(null)
        val activeResourceInfo = workbookViewModel.activeResourceInfoProperty.value

        createTabs()
        tabPane.tabs.addAll(tabMap.values)

        // Adding these tabs can change the active resource property so we need to
        // change it back to what it was originally
        if (activeResourceInfo != null) {
            restoreActiveResourceInfo(activeResourceInfo)
        }
    }

    private fun getTargetBookResourceInfo(): ResourceInfo {
        return workbookViewModel.workbook.target.resourceContainer.let {
            ResourceInfo(
                slug = it.identifier,
                title = it.title,
                type = it.type
            )
        }
    }

    private fun createTabs() {
        (sequenceOf(getTargetBookResourceInfo()) + workbookViewModel.workbook.source.subtreeResources)
            .forEach { info ->
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
