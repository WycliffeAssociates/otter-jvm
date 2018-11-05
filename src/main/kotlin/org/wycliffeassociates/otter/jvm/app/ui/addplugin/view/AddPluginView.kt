package org.wycliffeassociates.otter.jvm.app.ui.addplugin.view

import com.jfoenix.controls.JFXButton
import com.jfoenix.controls.JFXCheckBox
import javafx.geometry.Pos
import org.wycliffeassociates.otter.jvm.app.ui.addplugin.AddPluginStyles
import org.wycliffeassociates.otter.jvm.app.ui.addplugin.viewmodel.AddPluginViewModel
import tornadofx.*

class AddPluginView : View() {
    init {
        title = messages["addPlugin"]
        importStylesheet<AddPluginStyles>()
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
                add(JFXButton(messages["browse"].toUpperCase()).apply {
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
                })
            }
            field(messages["capabilities"]) {
                add(JFXCheckBox(messages["canRecord"])
                        .apply { viewModel.canRecordProperty.bind(selectedProperty()) })
                add(JFXCheckBox(messages["canEdit"])
                        .apply { viewModel.canEditProperty.bind(selectedProperty()) })
            }
        }
        hbox {
            alignment = Pos.TOP_RIGHT
            add(JFXButton(messages["save"].toUpperCase()).apply {
                addClass(AddPluginStyles.saveButton)
                action {
                    viewModel.save()
                    close()
                }
                enableWhen { viewModel.validated() }
            })
        }
    }
}