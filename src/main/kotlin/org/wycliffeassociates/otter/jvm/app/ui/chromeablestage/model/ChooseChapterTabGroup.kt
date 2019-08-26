package org.wycliffeassociates.otter.jvm.app.ui.chromeablestage.model

import javafx.scene.control.Tab
import org.wycliffeassociates.otter.jvm.app.ui.cardgrid.view.CardGridFragment
import org.wycliffeassociates.otter.jvm.app.ui.projectwizard.view.SlugsEnum
import org.wycliffeassociates.otter.jvm.utils.startWith
import tornadofx.*

class ChooseChapterTabGroup : TabGroup() {
    private val tabMap: MutableMap<String, Tab> = mutableMapOf()

    override fun activate() {
        workbookViewModel.workbook.source.subtreeResources
            .map { it.slug }
            .startWith(SlugsEnum.ULB.slug)
            .map { slug ->
                tabMap.putIfAbsent(slug, ChapterSelectTab(slug))
            }

        tabPane.tabs.addAll(tabMap.values)

        selectAppropriateTab()
    }

    private fun selectAppropriateTab() {
        workbookViewModel.activeResourceSlugProperty.value?.let { activeSlug ->
            tabMap[activeSlug]?.select()
        }
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
