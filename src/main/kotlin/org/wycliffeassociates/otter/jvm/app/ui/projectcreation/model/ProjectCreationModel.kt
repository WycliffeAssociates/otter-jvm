package org.wycliffeassociates.otter.jvm.app.ui.projectcreation.model


import com.github.thomasnield.rxkotlinfx.observeOnFx
import io.reactivex.Single
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler
import io.reactivex.schedulers.Schedulers
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.jvm.app.ui.chapterPage.model.Project
import org.wycliffeassociates.otter.jvm.app.ui.chapterpage.model.Project

import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import org.wycliffeassociates.otter.jvm.persistence.repositories.CollectionRepository
import org.wycliffeassociates.otter.jvm.persistence.repositories.LanguageRepository
import org.wycliffeassociates.otter.jvm.persistence.repositories.ProjectRepository
import org.wycliffeassociates.otter.jvm.persistence.repositories.SourceRepository
import org.wycliffeassociates.otter.jvm.usecases.CreateProjectUseCase
import tornadofx.*

class ProjectCreationModel {
    val creationUseCase = CreateProjectUseCase(LanguageRepository(Injector.database), SourceRepository(Injector.database), CollectionRepository(Injector.database))
    var sourceLanguageProperty: Language by property()
    var targetLanguageProperty: Language by property()
    var resourceSelected: Collection by property()

    val languages: Single<List<Language>>
        get() = creationUseCase.getAllLanguages()
                .observeOnFx()

    val resources: Single<List<Collection>>
        get() = creationUseCase.getSourceRepos()
                .observeOnFx()
    val all: Single<List<Collection>>
        get() = creationUseCase.getAll()
                .observeOnFx()
//    var resource by resourceProperty
    // var resourceProperty = getProperty(ProjectCreationModel::resourceSelected)

    private var project: ObservableList<Project> by property(
            FXCollections.observableList(ProjectList().projectList
            )
    )
    var projectProperty = getProperty(ProjectCreationModel::project)

    /*

     */

    init {

    }

}