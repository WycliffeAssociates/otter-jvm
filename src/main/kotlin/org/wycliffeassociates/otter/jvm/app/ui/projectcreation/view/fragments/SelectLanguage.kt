package org.wycliffeassociates.otter.jvm.app.ui.projectcreation.view.fragments

import javafx.geometry.Pos
import javafx.scene.layout.HBox
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.jvm.app.ui.languageSelectorFragment.LanguageSelectionItem
import org.wycliffeassociates.otter.jvm.app.ui.projectcreation.viewmodel.ProjectCreationViewModel
import org.wycliffeassociates.otter.jvm.app.widgets.filterableComboBox.ComboBoxSelectionItem
import org.wycliffeassociates.otter.jvm.app.widgets.filterableComboBox.FilterableComboBox
import tornadofx.*
import tornadofx.Stylesheet.Companion.root

class SelectLanguage : View() {
    val model = SelectLanguageModel()
    val viewModel: ProjectCreationViewModel by inject()
    val selectionData: List<ComboBoxSelectionItem>

    init {
        selectionData = model.languageVals.map { LanguageSelectionItem(it) }
    }

    override val root = hbox {
        alignment = Pos.CENTER
        style {
            padding = box(20.0.px)
        }

        hbox(100.0) {
            anchorpaneConstraints {
                leftAnchor = 50.0
                topAnchor = 250.0
            }
            setPrefSize(600.0, 200.0)

            this += FilterableComboBox(selectionData, "Select a Source Language", viewModel::sourceLanguage)
            this += FilterableComboBox(selectionData, "Select a Target Language", viewModel::targetLanguage)
        }

//                button("Confirm") {
//                    anchorpaneConstraints {
//                        rightAnchor = 30.0
//                        bottomAnchor = 50.0
//                    }
//                    isDisable = true
//                    viewModel.sourceLanguageProperty.onChange {
//                        isDisable = viewModel.targetLanguageProperty.value.equals("")
//                    }
//
//                    viewModel.targetLanguageProperty.onChange {
//                        isDisable = viewModel.sourceLanguageProperty.value.equals("")
//
//                    }
//                    action {
//                        viewModel.setActiveId(viewModel.activeIdProperty.value + 1)
//                    }
//                }
    }
}

class SelectLanguageModel {
    val languageVals = listOf(
            Language(0, "ENG", "English", true, "0"),
            Language(0, "MAN", "Mandarin", true, "1"),
            Language(0, "ESP", "Spanish", true, "2"),
            Language(0, "ARA", "Arabic", true, "3"),
            Language(0, "RUS", "Russian", true, "4"),
            Language(0, "AAR", "Afrikaans", true, "5"),
            Language(0, "HEB", "Hebrew", true, "6"),
            Language(0, "JAP", "Japanese", true, "7"),
            Language(0, "FRN", "French", true, "8")
    )

}