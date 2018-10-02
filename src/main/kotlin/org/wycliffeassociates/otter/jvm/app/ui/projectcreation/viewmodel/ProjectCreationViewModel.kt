package org.wycliffeassociates.otter.jvm.app.ui.projectcreation.viewmodel

import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.jvm.app.ui.projectcreation.model.ProjectCreationModel
import tornadofx.*

class ProjectCreationViewModel : ItemViewModel<ProjectCreationModel>(ProjectCreationModel()) {

    var sourceLanguage = bind(ProjectCreationModel::sourceLanguageProperty, true)
    var targetLanguage = bind(ProjectCreationModel::targetLanguageProperty, true)


    var resource = bind(ProjectCreationModel::resourceSelected, true)
    val resourceListProperty= bind(ProjectCreationModel::resources)
    var languageList: List<Language>

    val projectsProperty = bind(ProjectCreationModel::projectProperty)

    init {
        languageList = listOf()
         resourceListProperty.value.doOnSuccess{
            languageList = it
        }

    }
//    fun sourceLanguage(selection: ComboBoxSelectionItem) {
//        println(selection.labelText)
//       // sourceLanguage.set(selection.labelText)
//    }

//    fun targetLanguage(selection: ComboBoxSelectionItem) {
//        println(selection.labelText)
//      //  targetLanguage.set(selection.labelText)
//    }
}