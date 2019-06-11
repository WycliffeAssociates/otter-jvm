package org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.view

import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import javafx.scene.layout.GridPane
import javafx.scene.layout.RowConstraints
import org.wycliffeassociates.otter.common.data.workbook.Take
import org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.viewmodel.TakesViewModel
import org.wycliffeassociates.otter.jvm.app.ui.takemanagement.viewmodel.TakeManagementViewModel
import tornadofx.*

class TakesTabContent(
    takesList: ObservableList<Take>
) : Fragment() {

    private val takeManagementViewModel: TakeManagementViewModel by inject()
    private val takesViewModel: TakesViewModel by inject()

    val formattedTextProperty = SimpleStringProperty()

    init {
        importStylesheet<ResourceTakesStyles>()
    }

    private fun GridPane.setFillHeightSingleRow() {
        val rc = RowConstraints()
        rc.percentHeight = 100.0
        rowConstraints.addAll(rc)
    }

    override val root = gridpane {
        addClass(ResourceTakesStyles.main)
        setFillHeightSingleRow()

        row {
            vbox {
                gridpaneColumnConstraints {
                    percentWidth = 50.0
                }
                addClass(ResourceTakesStyles.leftRegionContainer)
                add(
                    TabContentLeftRegion(formattedTextProperty, takesViewModel::newTakeAction)
                )
            }
            vbox(20.0) {
                gridpaneColumnConstraints {
                    percentWidth = 50.0
                }
                addClass(ResourceTakesStyles.rightRegion)
                add(
                    TakesListView(takesList, takeManagementViewModel::audioPlayer)
                )
            }
        }
    }
}