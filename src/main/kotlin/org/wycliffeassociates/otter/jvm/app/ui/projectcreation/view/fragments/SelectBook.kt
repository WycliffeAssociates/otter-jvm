package org.wycliffeassociates.otter.jvm.app.ui.projectcreation.view.fragments

import javafx.geometry.Pos
import javafx.scene.layout.HBox
import org.wycliffeassociates.otter.jvm.app.ui.chapterPage.model.Project
import org.wycliffeassociates.otter.jvm.app.ui.projectcreation.viewmodel.ProjectCreationViewModel
import tornadofx.*
import tornadofx.Stylesheet.Companion.root

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