package org.wycliffeassociates.otter.jvm.app.ui.projectcreation.viewmodel

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.jvm.app.ui.projectcreation.model.ProjectCreationModel
import tornadofx.*

class ProjectCreationViewModel : ItemViewModel<ProjectCreationModel>(ProjectCreationModel()) {

    var sourceLanguage = bind(ProjectCreationModel::sourceLanguageProperty, true)
    var targetLanguage = bind(ProjectCreationModel::targetLanguageProperty, true)


    var resource = bind(ProjectCreationModel::resourceSelected, true)
    val languagesListProperty= bind(ProjectCreationModel::languages)
    var languageList: ObservableList<Language> = FXCollections.observableArrayList()

    val resourceListProperty = bind(ProjectCreationModel::resources)
    var resourceList : ObservableList<Collection> = FXCollections.observableArrayList()

    val projectsProperty = bind(ProjectCreationModel::projectProperty)

    init {

        languagesListProperty.value.map {
            languageList.setAll(it)
        }.subscribe()

        resourceListProperty.value.map {
            resourceList.setAll(it)
        }.subscribe()

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