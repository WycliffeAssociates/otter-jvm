package org.wycliffeassociates.otter.jvm.app.ui.projecthome

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Pos
import org.wycliffeassociates.otter.jvm.app.ui.projectcreation.view.ProjectCreationWizard
import tornadofx.*

class ProjectHomeView : View() {
    val viewModel: ProjectHomeViewModel by inject()
    override val root = borderpane {
        style {
            setPrefSize(1200.0, 800.0)
        }
        center = datagrid(viewModel.allProjectsList) {
            cellCache = {
                vbox {
                    label(it.labelKey)
                    label(it.titleKey)
                    label(it.slug)
                }
            }
        }

        bottom = hbox {
            style {
                padding = box(15.0.px)
            }

            alignment = Pos.BOTTOM_RIGHT
            button("", MaterialIconView(MaterialIcon.ADD)) {
                style {
                    padding = box(15.0.px)
                }
                action {
                    openModal()
                }
            }
        }

    }
}