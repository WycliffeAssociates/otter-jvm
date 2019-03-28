package org.wycliffeassociates.otter.jvm.resourcecardpage.view

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jfoenix.controls.JFXCheckBox
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import org.wycliffeassociates.otter.common.data.model.AssociatedAudio
import org.wycliffeassociates.otter.common.data.model.Resource
import org.wycliffeassociates.otter.common.data.model.ResourceGroup
import org.wycliffeassociates.otter.common.data.model.TextItem
import org.wycliffeassociates.otter.jvm.app.theme.AppStyles
import org.wycliffeassociates.otter.jvm.app.ui.mainscreen.view.MainScreenStyles
import org.wycliffeassociates.otter.jvm.resourcecardpage.styles.ResourceCardStyles
import org.wycliffeassociates.otter.jvm.resourcecardpage.viewmodel.ResourcesViewModel
import tornadofx.*

class ResourcePageFragment : Fragment() {

    var resourceGroups: ObservableList<ResourceGroup> = FXCollections.observableList(mutableListOf())

    init {
        importStylesheet<MainScreenStyles>()
        importStylesheet<ResourceCardStyles>()

        val groups: MutableList<ResourceGroup> = mutableListOf()
        for (i in 0..175) {
            groups.add(ResourceGroup(createListOfResources(i, (i % 5) + 1), "Verse $i Resources"))
        }
        resourceGroups.addAll(groups)
    }

    private fun createListOfResources(verseNum: Int, n: Int): List<Resource> {
        val list: MutableList<Resource> = mutableListOf()
        for (i in 1..n) {
            list.add(resource(verseNum, i))
        }
        return list
    }

    override val root = vbox {

        addClass(MainScreenStyles.main)

        // TODO: This is the top progress bar banner
        vbox {
            addClass(ResourceCardStyles.statusBarBanner)
            spacing = 10.0
            hbox {
                label("Translation Notes") {
                    graphic = AppStyles.tNGraphic()
                }
                region {
                    hgrow = Priority.ALWAYS
                }
                add(
                        JFXCheckBox("Hide Completed").apply {
                            isDisableVisualFocus = true
                        }
                )
            }
            // TODO: Status bar
            region {
                hgrow = Priority.ALWAYS
                style {
                    backgroundColor += Color.ORANGE
                    prefHeight = 8.px
                }
            }
        }

        listview(resourceGroups) {
            vgrow = Priority.ALWAYS // This needs to be here
            cellFormat {
                graphic = resourcegroupcard(it)
            }
            isFocusTraversable = false
            addClass(ResourceCardStyles.resourceGroupList)
        }
    }

    private fun resource(verseNum: Int, resourceNum: Int): Resource {
        val titleAudio = AssociatedAudio(listOf(), BehaviorRelay.create())
        val bodyAudio = AssociatedAudio(listOf(), BehaviorRelay.create())
//        val bodyAudio = null
        titleAudio.selected.accept(1)
        bodyAudio.selected.accept(1)
        return Resource(
                TextItem("type", "Verse $verseNum, Title $resourceNum", ".txt"),
                null,
                0,
                titleAudio,
                bodyAudio
        )
    }
}