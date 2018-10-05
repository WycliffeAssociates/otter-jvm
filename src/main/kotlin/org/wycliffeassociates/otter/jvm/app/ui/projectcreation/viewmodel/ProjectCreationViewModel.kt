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


    var selectedResourceProperty = bind(ProjectCreationModel::selectedResource, true)
    var selectedAnthologyProperty = bind(ProjectCreationModel::selectedAnthology, true)
    val selectedBookProperty = bind(ProjectCreationModel::selectedBook, true)

    val languagesListProperty= bind(ProjectCreationModel::languages)
    var languageList: ObservableList<Language> = FXCollections.observableArrayList()

    val resourceListProperty = bind(ProjectCreationModel::resources)
    var resourceList : ObservableList<Collection> = FXCollections.observableArrayList()

    val allProperty = bind(ProjectCreationModel::all)
    var allList: ObservableList<Collection> = FXCollections.observableArrayList()

    val anthologyListProperty = bind(ProjectCreationModel::anthologyList, true)
    val anthologyList = FXCollections.observableArrayList<Collection>()

    val bookListProperty = bind(ProjectCreationModel::bookList, true)
    val bookList = FXCollections.observableArrayList<Collection>()

    val projectsProperty = bind(ProjectCreationModel::projectProperty)

    init {

        languagesListProperty.value.map {
            languageList.setAll(it)
        }.subscribe()

        resourceListProperty.value.map {
            resourceList.setAll(it)
        }.subscribe()

        allProperty.value.map {
            allList.setAll(it)
        }.subscribe()

        anthologyListProperty.onChange {
            anthologyList.setAll(it)
        }

        bookListProperty.onChange {
            bookList.setAll(it)
        }
    }

    fun getResourceChildren() = bind(ProjectCreationModel::getResourceChildren)
    fun getBooks() = bind(ProjectCreationModel::getBooks)
    fun createProject() = bind(ProjectCreationModel::createProject)

}