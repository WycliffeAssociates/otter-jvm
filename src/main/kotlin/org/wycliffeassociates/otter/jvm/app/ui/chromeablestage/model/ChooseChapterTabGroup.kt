package org.wycliffeassociates.otter.jvm.app.ui.chromeablestage.model

import javafx.scene.control.Tab
import org.wycliffeassociates.otter.jvm.app.ui.cardgrid.view.CardGridFragment
import org.wycliffeassociates.otter.jvm.app.ui.projectwizard.view.SlugsEnum
import org.wycliffeassociates.otter.jvm.utils.startWith
import tornadofx.*

class ChooseChapterTabGroup : TabGroup() {
    private val tabMap: MutableMap<String, Tab> = mutableMapOf()

    override fun activate() {
        workbookViewModel.activeChapterProperty.set(null)
        val currentActiveResourceSlug = workbookViewModel.activeResourceSlugProperty.value

        createTabs()
        tabPane.tabs.addAll(tabMap.values)

        // Adding these tabs can change the active resource slug property so we need to
        // change it back to what it was originally
        if (currentActiveResourceSlug != null) {
            restoreActiveResourceSlug(currentActiveResourceSlug)
        }
    }

    private fun createTabs() {
        workbookViewModel.workbook.source.subtreeResources
            .map { it.slug }
            .startWith(SlugsEnum.ULB.slug)
            .map { slug ->
                tabMap.putIfAbsent(slug, ChapterSelectTab(slug))
            }
    }

    private fun restoreActiveResourceSlug(slug: String) {
        workbookViewModel.activeResourceSlugProperty.set(slug)
        tabMap[slug]?.select()
    }

    private inner class ChapterSelectTab(val slug: String): Tab() {
        init {
            text = slug
            add(CardGridFragment().root)
            onSelected {
                workbookViewModel.activeResourceSlugProperty.set(slug)
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
