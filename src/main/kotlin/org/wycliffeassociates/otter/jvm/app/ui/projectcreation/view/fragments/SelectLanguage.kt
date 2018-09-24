package org.wycliffeassociates.otter.jvm.app.ui.projectcreation.view.fragments


import javafx.geometry.Pos
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.jvm.app.ui.projectcreation.viewmodel.ProjectCreationViewModel
import tornadofx.*

class SelectLanguage: Fragment() {
    val model = SelectLanguageModel()

    val viewModel: ProjectCreationViewModel by inject()
    val selectionData: List<Language>

    override val complete = viewModel.valid(viewModel.sourceLanguage, viewModel.targetLanguage)

    init {
        selectionData = model.languageVals.map { it }
    }
    override  val root =  anchorpane {
        hbox{
            this+= FilterableComboBox(selectionData,"this is your hint", ::println)
            this+= FilterableComboBox(selectionData,"this is your hint", ::println)
        }

        button("Confirm"){
            anchorpaneConstraints {
                rightAnchor = 30.0
                bottomAnchor = 50.0
            }
 setPrefSize(600.0, 200.0)

            combobox(viewModel.sourceLanguage,values = selectionData)
            combobox(viewModel.targetLanguage, values =selectionData)
        }
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