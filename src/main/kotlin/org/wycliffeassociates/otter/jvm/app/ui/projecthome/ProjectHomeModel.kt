package org.wycliffeassociates.otter.jvm.app.ui.projecthome

import com.github.thomasnield.rxkotlinfx.observeOnFx
import javafx.collections.FXCollections
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import org.wycliffeassociates.otter.common.domain.usecases.ProjectUseCase
import org.wycliffeassociates.otter.jvm.usecases.ProjectUseCase

class ProjectHomeModel {
    val projectUseCase = ProjectUseCase(Injector.projectRepo)

        val allProjects = FXCollections.observableArrayList<Collection>()

    init {
        getAllProjects()
    }

    fun getAllProjects() {
        projectUseCase.getAllRoot()
                .observeOnFx()
                .doOnSuccess {
                    allProjects.setAll(it)
                }.subscribe()
    }
}