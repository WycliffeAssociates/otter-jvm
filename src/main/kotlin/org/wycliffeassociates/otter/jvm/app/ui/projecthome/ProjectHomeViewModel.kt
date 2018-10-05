package org.wycliffeassociates.otter.jvm.app.ui.projecthome

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.jvm.app.ui.projectcreation.view.ProjectCreationWizard
import tornadofx.*

class ProjectHomeViewModel : ItemViewModel<ProjectHomeModel>(ProjectHomeModel()) {

    val allProjectsProperty = bind(ProjectHomeModel::allProjects)
//    var allProjectsList : ObservableList<Collection> = FXCollections.observableArrayList()

    init {
//        allProjectsProperty.value.map {
//            allProjectsList.setAll(it)
//        }
    }

    fun createProject() {
        find<ProjectCreationWizard> {
            openModal()
        }
    }

}
