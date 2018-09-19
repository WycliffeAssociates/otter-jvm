package org.wycliffeassociates.otter.jvm.app.ui.projectcreation.viewmodel

import org.wycliffeassociates.otter.jvm.app.ui.projectcreation.model.ProjectCreationModel
import tornadofx.*

class ProjectCreationViewModel : ViewModel() {

    val model = ProjectCreationModel()

    val activeIdProperty = bind { model.actveIdProperty }
    val activeViewProperty = bind { model.activeViewProperty }
}