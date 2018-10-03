package org.wycliffeassociates.otter.jvm.app.ui.projecthome

import com.github.thomasnield.rxkotlinfx.observeOnFx
import io.reactivex.Single
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import org.wycliffeassociates.otter.jvm.persistence.repositories.ProjectRepository
import org.wycliffeassociates.otter.jvm.usecases.ProjectUseCase

class ProjectHomeModel {
    val projectUseCase = ProjectUseCase(ProjectRepository(Injector.database))

        val allProjects : Single<List<Collection>>
        get() =  projectUseCase.getAllRoot()
                .observeOnFx()
}