package org.wycliffeassociates.otter.jvm.app.ui.resourcetakes.view

import com.jakewharton.rxrelay2.BehaviorRelay
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.layout.RowConstraints
import javafx.scene.paint.Color
import org.wycliffeassociates.otter.common.data.model.MimeType
import org.wycliffeassociates.otter.common.data.workbook.Take
import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
import org.wycliffeassociates.otter.jvm.app.ui.takemanagement.viewmodel.TakeManagementViewModel
import org.wycliffeassociates.otter.jvm.app.widgets.highlightablebutton.highlightablebutton
import org.wycliffeassociates.otter.jvm.app.widgets.takecard.TakeEvent
import org.wycliffeassociates.otter.jvm.app.widgets.takecard.TakeCard
import org.wycliffeassociates.otter.jvm.app.widgets.takecard.resourcetakecard
import tornadofx.*
import java.io.File
import java.time.LocalDate

class ResourceTakesView : Fragment() {

    private val viewModel: TakeManagementViewModel by inject()

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
                    label("Nullam a iaculis turpis asjdfkasjdfkasjdfk awefjioa jfiawef adfjiawef joawef asdmvioawef masdofu asfjiowejfkaj ajweifojadf jaiwoef asjdkfa jioawef jksladf jioawef jklsadjfoiawefajdlkfjaoiwefja iaculis turpis asjdfkasjdfkasjdfk awefjioa jfiawef adfjiawef joawef asdmvioawef masdofu asfjiowejfkaj ajweifojadf jaiwoef asjdkfa jioawef jksladf jioawef jklsadjfoiawefajdlkfjaoiwefja iaculis turpis asjdfkasjdfkasjdfk awefjioa jfiawef adfjiawef joawef asdmvioawef masdofu asfjiowejfkaj ajweifojadf jaiwoef asjdkfa jioawef jksladf jioawef jklsadjfoiawefajdlkfjaoiwefja iaculis turpis asjdfkasjdfkasjdfk awefjioa jfiawef adfjiawef joawef asdmvioawef masdofu asfjiowejfkaj ajweifojadf jaiwoef asjdkfa jioawef jksladf jioawef jklsadjfoiawefajdlkfjaoiwefja iaculis turpis asjdfkasjdfkasjdfk awefjioa jfiawef adfjiawef joawef asdmvioawef masdofu asfjiowejfkaj ajweifojadf jaiwoef asjdkfa jioawef jksladf jioawef jklsadjfoiawefajdlkfjaoiwefja") {
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

                addEventHandler(TakeEvent.PLAY) {
                    children.filterIsInstance<TakeCard>().forEach { card ->
                        if (it.target != card) {
                            card.fireEvent(TakeEvent(TakeEvent.PAUSE))
                        }
                    }
                }

                add(
                    resourcetakecard(
                        Take(
                            "take1.wav",
                            File("src//main//kotlin//org//wycliffeassociates//otter//jvm//resourcestestapp//takefiles//take1.wav"),
                            1,
                            MimeType.MARKDOWN,
                            LocalDate.parse("2019-04-15"),
                            BehaviorRelay.create()
                        ),
                        viewModel.audioPlayer()
                    )
                )
                add(
                    resourcetakecard(
                        Take(
                            "take2",
                            File("src//main//kotlin//org//wycliffeassociates//otter//jvm//resourcestestapp//takefiles//take2.wav"),
                            2,
                            MimeType.MARKDOWN,
                            LocalDate.parse("2019-04-16"),
                            BehaviorRelay.create()
                        ),
                        viewModel.audioPlayer()
                    )
                )
            }
        }
    }
}
