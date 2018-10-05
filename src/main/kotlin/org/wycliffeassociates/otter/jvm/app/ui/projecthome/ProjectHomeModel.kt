package org.wycliffeassociates.otter.jvm.app.ui.projecthome

import com.github.thomasnield.rxkotlinfx.observeOnFx
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import org.wycliffeassociates.otter.jvm.persistence.repositories.ProjectRepository
import org.wycliffeassociates.otter.jvm.usecases.CreateProjectUseCase
import org.wycliffeassociates.otter.jvm.usecases.ProjectUseCase

class ProjectHomeModel {
    val projectUseCase = ProjectUseCase(Injector.projectRepo)

        val allCollections : Single<List<Collection>>
        get() =  projectUseCase.getAll().observeOnFx()

        val allProjects = FXCollections.observableArrayList<Collection>()

    init {
        allCollections.doOnSuccess{
            projectUseCase.getChildren(it[3]).observeOnFx().doOnSuccess{
                allProjects.setAll(it)
            }.subscribe()
        }.subscribe()
    }
}