package org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.view

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.layout.RowConstraints
import javafx.scene.paint.Color
import org.wycliffeassociates.otter.common.data.workbook.Take
import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
import org.wycliffeassociates.otter.jvm.app.ui.takemanagement.viewmodel.TakeManagementViewModel
import org.wycliffeassociates.otter.jvm.app.widgets.highlightablebutton.highlightablebutton
import tornadofx.*

class ResourceTakesTabFragment(
    formattedTextProperty: StringProperty,
    takesList: ObservableList<Take>
) : Fragment() {

    private val takeManagementViewModel: TakeManagementViewModel by inject()

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
                addClass(ResourceTakesStyles.leftRegion)

                vbox {
                    addClass(ResourceTakesStyles.dragTarget)
                    text("Drag Take Here")
                }

                scrollpane {
                    isFitToWidth = true
                    vgrow = Priority.ALWAYS
                    addClass(ResourceTakesStyles.contentScrollPane)
//                    label("Nullam a iaculis turpis asjdfkasjdfkasjdfk awefjioa jfiawef adfjiawef joawef asdmvioawef masdofu asfjiowejfkaj ajweifojadf jaiwoef asjdkfa jioawef jksladf jioawef jklsadjfoiawefajdlkfjaoiwefja iaculis turpis asjdfkasjdfkasjdfk awefjioa jfiawef adfjiawef joawef asdmvioawef masdofu asfjiowejfkaj ajweifojadf jaiwoef asjdkfa jioawef jksladf jioawef jklsadjfoiawefajdlkfjaoiwefja iaculis turpis asjdfkasjdfkasjdfk awefjioa jfiawef adfjiawef joawef asdmvioawef masdofu asfjiowejfkaj ajweifojadf jaiwoef asjdkfa jioawef jksladf jioawef jklsadjfoiawefajdlkfjaoiwefja iaculis turpis asjdfkasjdfkasjdfk awefjioa jfiawef adfjiawef joawef asdmvioawef masdofu asfjiowejfkaj ajweifojadf jaiwoef asjdkfa jioawef jksladf jioawef jklsadjfoiawefajdlkfjaoiwefja iaculis turpis asjdfkasjdfkasjdfk awefjioa jfiawef adfjiawef joawef asdmvioawef masdofu asfjiowejfkaj ajweifojadf jaiwoef asjdkfa jioawef jksladf jioawef jklsadjfoiawefajdlkfjaoiwefja") {
                    label(formattedTextProperty) {
                        isWrapText = true
                        addClass(ResourceTakesStyles.contentText)
                    }
                }

                vbox {
                    addClass(ResourceTakesStyles.newTakeRegion)
                    add(
                        highlightablebutton {
                            highlightColor = Color.ORANGE
                            secondaryColor = AppTheme.colors.white
                            isHighlighted = true
                            graphic = MaterialIconView(MaterialIcon.MIC_NONE, "25px")
                            maxWidth = 500.0
                            text = messages["newTake"]
                        }
                    )
                }
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