package org.wycliffeassociates.otter.jvm.app.ui.projecthome

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Pos
import org.wycliffeassociates.otter.jvm.app.UIColorsObject.Colors
import org.wycliffeassociates.otter.jvm.app.widgets.projectcard
import tornadofx.*

class ProjectHomeView : View() {

    val viewModel: ProjectHomeViewModel by inject()
    override val root = borderpane {
        top = hbox { alignment = Pos.CENTER_RIGHT
            button("Refresh") {
                prefWidth = 232.0
                prefHeight = 40.0
                action {
                    viewModel.getAllProjects()
                }
            }}
        style {
            setPrefSize(1200.0, 800.0)
        }
        center = datagrid(viewModel.allProjectsProperty.value) {
            horizontalCellSpacing = 10.0
            cellHeight = 250.0
            cellWidth = 232.0
            cellCache = {
               projectcard(it) {
                    buttonText = "Load Project"
                   style {
                       backgroundRadius += box(10.0.px)
                       borderRadius += box(10.0.px)
                   }
               }

        }
            style {
                accentColor = c(Colors["primary"])
            }
        }

        bottom = hbox {
            style {
                padding = box(25.0.px)
            }
            alignment = Pos.BOTTOM_RIGHT
            button("", MaterialIconView(MaterialIcon.ADD)) {
                style {
                    padding = box(15.0.px)
                    backgroundRadius += box(100.0.px)
                    borderRadius += box(100.0.px)
                }
                action {
                viewModel.createProject()
                }
            }
        }
    }

    override fun onDock() {
        viewModel.getAllProjects()
    }
}