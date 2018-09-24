package org.wycliffeassociates.otter.jvm.app.ui.projectcreation.model


import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.jvm.app.ui.chapterPage.model.Project
import tornadofx.*

class ProjectCreationModel {
    var sourceLanguageProperty: Language by property()

    var targetLanguageProperty: Language by property()


    var projectProperty = getProperty(ProjectCreationModel::project)

    /*
    TODO adding Resources, Filtering and Book Selection to Model
     */

}