package org.wycliffeassociates.otter.jvm.app.ui.addplugin.view

import javafx.geometry.Pos
import org.wycliffeassociates.otter.jvm.app.ui.addplugin.viewmodel.AddPluginViewModel
import tornadofx.*

class AddPluginView : View() {
    init {
        title = messages["addPlugin"]
    }

    private val viewModel: AddPluginViewModel by inject()

    override val root = form {
        prefWidth = 500.0
        fieldset {
            field(messages["name"]) {
                textfield(viewModel.nameProperty) {
                    validator { viewModel.validateName() }
                }
            }
            field(messages["executable"]) {
                textfield(viewModel.pathProperty) {
                    validator { viewModel.validatePath() }
                }
                button(messages["browse"]) {
                    action {
                        val files = chooseFile(
                                messages["chooseExecutable"],
                                arrayOf(),
                                FileChooserMode.Single
                        )
                        if (files.isNotEmpty()) {
                            viewModel.path = files.first().toString()
                        }
                    }
                }
            }
            field(messages["capabilities"]) {
                checkbox(messages["canRecord"], viewModel.canRecordProperty)
                checkbox(messages["canEdit"], viewModel.canEditProperty)
            }
        }
        hbox {
            alignment = Pos.TOP_RIGHT
            button(messages["save"]) {
                action {
                    viewModel.save()
                    close()
                }
                enableWhen { viewModel.validated() }
            }
        }
    }
}