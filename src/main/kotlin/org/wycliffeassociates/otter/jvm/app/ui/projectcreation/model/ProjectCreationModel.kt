package org.wycliffeassociates.otter.jvm.app.ui.projectcreation.model

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.jvm.app.ui.chapterPage.model.Project
import tornadofx.*

class ProjectCreationModel {
    var sourceLanguage: String by property("")
    var sourceLanguageProperty = getProperty(ProjectCreationModel:: sourceLanguage)

    var targetLanguage: String by property("")
    var targetLanguageProperty = getProperty(ProjectCreationModel:: targetLanguage)

    private var project: ObservableList<Project> by property(
            FXCollections.observableList(ProjectList().projectList
            )
    )

    var projectProperty = getProperty(ProjectCreationModel::project)

    /*
    TODO adding Resources, Filtering and Book Selection to Model
     */
}