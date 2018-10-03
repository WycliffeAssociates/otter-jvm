package org.wycliffeassociates.otter.jvm.app.ui.projectcreation.view.fragments

import javafx.geometry.Pos
import org.wycliffeassociates.otter.jvm.app.ui.projectcreation.viewmodel.ProjectCreationViewModel
import tornadofx.*


class SelectBook() : View() {
    val viewModel: ProjectCreationViewModel by inject()

    override val root =
            datagrid(viewModel.allList) {
                style{
                    alignment= Pos.CENTER
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

    }
