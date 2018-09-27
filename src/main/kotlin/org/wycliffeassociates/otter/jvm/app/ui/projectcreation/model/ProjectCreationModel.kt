package org.wycliffeassociates.otter.jvm.app.ui.projectcreation.model

<<<<<<< HEAD

import javafx.beans.binding.BooleanExpression
import javafx.beans.property.SimpleStringProperty
=======
>>>>>>> building, opening ui
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Language
<<<<<<< HEAD
import org.wycliffeassociates.otter.jvm.app.ui.chapterPage.model.Project
=======
import org.wycliffeassociates.otter.jvm.app.ui.chapterpage.model.Project
>>>>>>> building, opening ui
import tornadofx.*

class ProjectCreationModel {
    var sourceLanguageProperty: Language by property()

    var targetLanguageProperty: Language by property()

    var resourceSelected : Collection by property()
//    var resource by resourceProperty
   // var resourceProperty = getProperty(ProjectCreationModel::resourceSelected)

    private var project: ObservableList<Project> by property(
            FXCollections.observableList(ProjectList().projectList
            )
    )

    var projectProperty = getProperty(ProjectCreationModel::project)

    /*
<<<<<<< HEAD
    TODO adding Resources, Filtering and Book Selection to Model
=======
    TODO adding Resources, Filtering and Book Selection to model
>>>>>>> building, opening ui
     */

}