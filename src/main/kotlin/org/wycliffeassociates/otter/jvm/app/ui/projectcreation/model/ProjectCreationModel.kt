package org.wycliffeassociates.otter.jvm.app.ui.projectcreation.model

import com.github.thomasnield.rxkotlinfx.observeOnFx
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javafx.beans.property.SimpleBooleanProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.common.data.model.ResourceMetadata
import org.wycliffeassociates.otter.common.data.model.SourceCollection
import org.wycliffeassociates.otter.jvm.app.ui.chapterpage.model.Project
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import org.wycliffeassociates.otter.jvm.persistence.repositories.CollectionRepository
import org.wycliffeassociates.otter.jvm.persistence.repositories.LanguageRepository
import org.wycliffeassociates.otter.jvm.persistence.repositories.SourceRepository
import org.wycliffeassociates.otter.jvm.usecases.CreateProjectUseCase
import tornadofx.*

class ProjectCreationModel {
    private val creationUseCase = CreateProjectUseCase(Injector.languageRepo, Injector.sourceRepo, Injector.collectionRepo, Injector.projectRepo)
    var sourceLanguageProperty: Language by property()
    var targetLanguageProperty: Language by property()
    var selectedResource: Collection by property()
    var selectedAnthology: Collection by property()
    var selectedBook: Collection by property()
    var anthologyList: ObservableList<Collection> by property(FXCollections.observableArrayList())
    var bookList: ObservableList<Collection> by property(FXCollections.observableArrayList())

    val languages: Single<List<Language>>
        get() = creationUseCase.getAllLanguages()
                .observeOnFx()

    val resources: Single<List<Collection>>
        get() = creationUseCase.getSourceRepos()
                .observeOnFx()
    val all: Single<List<Collection>>
        get() = creationUseCase.getSourceRepos()
                .observeOnFx()

    private var project: ObservableList<Project> by property(
            FXCollections.observableList(ProjectList().projectList
            )
    )
    var projectProperty = getProperty(ProjectCreationModel::project)

    fun getResourceChildren() {
        creationUseCase.getResourceChildren(selectedResource)
                .observeOnFx()
                .doOnSuccess {
                    anthologyList.setAll(it)
                }
                .subscribe()
    }

    fun getBooks() {
        creationUseCase.getResourceChildren(selectedAnthology)
                .observeOnFx()
                .doOnSuccess {
                    bookList.setAll(it)
                }
                .subscribe()
    }

    fun createProject() {
        creationUseCase.newProject(Collection(selectedBook.sort,selectedBook.slug, "project",
                selectedBook.titleKey ,selectedBook.resourceContainer)).observeOnFx().doOnSuccess {
            creationUseCase.updateSource(it, selectedBook)

        }.subscribe()
    }
}

/*

 */
