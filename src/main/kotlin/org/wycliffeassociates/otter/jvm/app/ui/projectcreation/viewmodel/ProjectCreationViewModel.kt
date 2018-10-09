package org.wycliffeassociates.otter.jvm.app.ui.projectcreation.viewmodel

import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.jvm.app.ui.languageselectorfragment.toTextView
import org.wycliffeassociates.otter.jvm.app.ui.projectcreation.model.ProjectCreationModel
import org.wycliffeassociates.otter.jvm.app.widgets.filterablecombobox.ComboBoxSelectionItem
import tornadofx.*

class ProjectCreationViewModel : ItemViewModel<ProjectCreationModel>(ProjectCreationModel()) {

    var sourceLanguage = bind(ProjectCreationModel::sourceLanguageProperty, true)
    var targetLanguage = bind(ProjectCreationModel::targetLanguageProperty, true)

    var selectedResourceProperty = bind(ProjectCreationModel::selectedResource, true)
    var selectedAnthologyProperty = bind(ProjectCreationModel::selectedAnthology, true)
    val selectedBookProperty = bind(ProjectCreationModel::selectedBook, true)

    val languagesList = item.languages
    val anthologyList = item.anthologyList
    val bookList = item.bookList

    val allPagesComplete = SimpleBooleanProperty(false)

    init {
        selectedBookProperty.onChange {
            if (it != null) {
                allPagesComplete.set(true)
            }
        }
    }

    fun getResourceChildren() = bind(ProjectCreationModel::getResourceChildren)
    fun getBooks() = bind(ProjectCreationModel::getBooks)
    fun createProject() = bind(ProjectCreationModel::createProject)
}