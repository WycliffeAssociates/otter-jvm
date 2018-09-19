package org.wycliffeassociates.otter.jvm.app.ui.projectcreation.model

import javafx.scene.Node
import javafx.scene.layout.HBox
import tornadofx.*

class ProjectCreationModel {
    var activeId : Int by property(-1)
    var actveIdProperty = getProperty(ProjectCreationModel::activeId)

    var activeView: Node by property(HBox())
    var activeViewProperty = getProperty(ProjectCreationModel::activeView)


}