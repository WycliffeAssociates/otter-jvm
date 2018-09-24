package org.wycliffeassociates.otter.jvm.app.ui.projectcreation.view.fragments

import org.wycliffeassociates.otter.common.data.model.Project
import org.wycliffeassociates.otter.jvm.app.widgets.ProjectCard
import tornadofx.*


class SelectBook() : View() {
    val viewModel: ProjectCreationViewModel by inject()
    //val root = DataGrid<Project>()

    override val root = hbox {
        //add(root)
        viewModel.projectsProperty.onChange {
            datagrid(it) {
                cellCache {
                    vbox(10) {
                        alignment = Pos.CENTER
                        label(it.book.title)
                        button()
                    }
                }
            }
        }
    }
}
