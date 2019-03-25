package org.wycliffeassociates.otter.jvm.resourcecardpage.view

import com.jfoenix.controls.JFXButton
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.scene.layout.VBox
import org.wycliffeassociates.otter.common.data.model.AssociatedAudio
import org.wycliffeassociates.otter.common.data.model.Resource
import org.wycliffeassociates.otter.common.data.model.TextItem
import org.wycliffeassociates.otter.jvm.app.ui.mainscreen.view.MainScreenStyles
import org.wycliffeassociates.otter.jvm.resourcecardpage.styles.ResourceCardStyles
import org.wycliffeassociates.otter.jvm.resourcecardpage.viewmodel.ResourcePageViewModel
import tornadofx.*

class ResourcePageView : View() {

    private val viewModel: ResourcePageViewModel by inject()

    var listResources: ObservableList<Resource> = FXCollections.observableList(mutableListOf())

    private val aResource = resource(0)

    private val scrollVBox = vbox {
        add(
                resourcecard(aResource) { }
        )

//        datagrid(listResources) {
//            cellCache {
//                resourcecard(it)
//            }
//        }

        listview(listResources) {
            cellFormat {
//                graphic = cache {
//                    resourcecard(it)
//                }
                graphic = resourcecard(it)
            }
        }

        add(
                JFXButton().apply {
                    //                    alignment = Pos.CENTER_RIGHT
                    text = "add to vbox"
                    onMousePressed = EventHandler {
                        addLotsOfResourcesToVbox()
                    }
                }
        )
        add(
                JFXButton().apply {
                    //                    alignment = Pos.CENTER_RIGHT
                    text = "add to list"
                    onMousePressed = EventHandler {
                        addLotsOfResourcesToListView()
                    }
                }
        )
    }

    init {
        importStylesheet<MainScreenStyles>()
        importStylesheet<ResourceCardStyles>()
    }

    override val root = scrollpane {
//    override val root = vbox {
        isFitToHeight = true
        isFitToWidth = true
        addClass(MainScreenStyles.main)

        add(
                scrollVBox
        )
    }

    private fun addLotsOfResourcesToVbox() {
        for (i in 1..100) {
            scrollVBox.add(resourcecard(resource(i)))
        }
    }

    private fun addLotsOfResourcesToListView() {
        val list: MutableList<Resource> = mutableListOf()
        for (i in 1..100) {
//            listResources.add(resource())
//            list.add(
            listResources.add(resource(i))
        }
//        listResources.addAll(list)
//        listResources.setAll(list)
    }

    private fun resource(i: Int): Resource {
        return Resource(
            TextItem("type", "Hello$i", ".txt"),
            null,
            0,
            AssociatedAudio(listOf(), null),
            null
        )
    }
}