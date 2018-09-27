package org.wycliffeassociates.otter.jvm.app.ui.projectcreation.view.fragments

<<<<<<< HEAD

=======
>>>>>>> building, opening ui
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Pos
import javafx.scene.paint.Color
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.jvm.app.ui.projectcreation.viewmodel.ProjectCreationViewModel
import tornadofx.*

<<<<<<< HEAD
class SelectLanguage: Fragment() {
    val model = SelectLanguageModel()

=======
class SelectLanguage : View() {
    val model = SelectLanguageModel()
>>>>>>> building, opening ui
    val viewModel: ProjectCreationViewModel by inject()
    val selectionData: List<Language>

    override val complete = viewModel.valid(viewModel.sourceLanguage, viewModel.targetLanguage)

    init {
        selectionData = model.languageVals.map { it }
    }

<<<<<<< HEAD

=======
>>>>>>> building, opening ui
    override val root = hbox {
        alignment = Pos.CENTER
        style {
            padding = box(100.0.px)
        }

<<<<<<< HEAD
        button("Confirm"){
            anchorpaneConstraints {
                rightAnchor = 30.0
                bottomAnchor = 50.0
            }
 setPrefSize(600.0, 200.0)
=======
        hbox(100.0) {
            anchorpaneConstraints {
                leftAnchor = 50.0
                topAnchor = 250.0
            }
            setPrefSize(600.0, 200.0)
>>>>>>> building, opening ui

            vbox{
               button("Target Language",MaterialIconView(MaterialIcon.RECORD_VOICE_OVER, "25px") ){
                   style {
                       backgroundColor += Color.TRANSPARENT
                   }
//                   isDisable = true
               }
                combobox(viewModel.sourceLanguage, selectionData).required()
            }

            vbox {

                button("Source Language",MaterialIconView(MaterialIcon.HEARING, "25px") ){
                    style {
                        backgroundColor += Color.TRANSPARENT
                    }
                   // isDisable = true
                }
                combobox(viewModel.targetLanguage, selectionData).required()

            }
        }
    }
}

class SelectLanguageModel {
<<<<<<< HEAD
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
=======
    val languageVals = listOf<Language>()
>>>>>>> building, opening ui

}