package org.wycliffeassociates.otter.jvm.app.ui.projecthome

import org.wycliffeassociates.otter.common.data.model.Project
import org.wycliffeassociates.otter.common.domain.GetProjectsUseCase
import tornadofx.*
import io.reactivex.Observable
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import org.wycliffeassociates.otter.jvm.app.widgets.ProjectCard

class ProjectHomeViewModel: ViewModel() {
    val projects : Observable<List<Project>> = Observable.just(emptyList())
    val items = FXCollections.observableArrayList<ProjectCard>()!!
    init {
        getProjects()
    }

    private fun getProjects() {
        projects.subscribe {
            items.setAll(
                    it.map{
                        ProjectCard(it)
                    }
            )
        }
    }
}
