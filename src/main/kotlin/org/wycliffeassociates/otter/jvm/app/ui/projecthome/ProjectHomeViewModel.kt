package org.wycliffeassociates.otter.jvm.app.ui.projecthome

import org.wycliffeassociates.otter.jvm.app.ui.chapterpage.view.ProjectPage
import org.wycliffeassociates.otter.jvm.app.ui.projectcreation.view.ProjectCreationWizard
import tornadofx.*

class ProjectHomeViewModel : ItemViewModel<ProjectHomeModel>(ProjectHomeModel()) {

    val allProjectsProperty = bind(ProjectHomeModel::allProjects)

     fun getAllProjects() = bind(ProjectHomeModel::getAllProjects)

    fun createProject() {
        find<ProjectCreationWizard> {
            openModal()
            onComplete {
                getAllProjects()
                //creationViewModel.createProject()
                //workspace.dockInNewScope<ProjectHomeView>()
            }
        }
    }


}
