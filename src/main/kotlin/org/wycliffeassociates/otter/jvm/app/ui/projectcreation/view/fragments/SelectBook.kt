package org.wycliffeassociates.otter.jvm.app.ui.projectcreation.view.fragments

import javafx.event.ActionEvent
import javafx.geometry.Pos
import javafx.scene.paint.Color
import org.wycliffeassociates.otter.jvm.app.UIColorsObject.Colors
import org.wycliffeassociates.otter.jvm.app.ui.projectcreation.viewmodel.ProjectCreationViewModel
import tornadofx.*


class SelectBook : View() {
    val viewModel: ProjectCreationViewModel by inject()

    override val root =
            datagrid(viewModel.bookList) {
                style{
                    alignment= Pos.CENTER
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

    override fun onSave() {
        viewModel.selectedBookProperty.value = root.selectedItem
        viewModel.createProject()
    }
    }
