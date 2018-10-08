package org.wycliffeassociates.otter.jvm.app.ui.projectcreation.view.fragments

import javafx.geometry.Pos
import org.wycliffeassociates.otter.jvm.app.UIColorsObject.Colors
import org.wycliffeassociates.otter.jvm.app.ui.projectcreation.viewmodel.ProjectCreationViewModel
import org.wycliffeassociates.otter.jvm.app.ui.projecthome.ProjectHomeView
import tornadofx.*


class SelectBook : View() {
    val viewModel: ProjectCreationViewModel by inject()

    override val root =
            datagrid(viewModel.bookList) {
                bindSelected(viewModel.selectedBookProperty)
                style {
                    alignment = Pos.CENTER
                    accentColor = c(Colors["primary"])
                }
                cellCache {
                    vbox(10) {
                        alignment = Pos.CENTER
                        label(it.titleKey)
                        label(it.labelKey)
                        button(messages["select"])
                    }
                }
            }
//
    override fun onSave() {
        runAsync{
            viewModel.createProject()
        }
    }
}
