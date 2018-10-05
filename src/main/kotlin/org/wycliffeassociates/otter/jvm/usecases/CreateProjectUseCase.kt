package org.wycliffeassociates.otter.jvm.usecases

import io.reactivex.Single
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.common.data.model.Resource
import org.wycliffeassociates.otter.common.data.model.SourceCollection
import org.wycliffeassociates.otter.jvm.persistence.repositories.CollectionRepository
import org.wycliffeassociates.otter.jvm.persistence.repositories.LanguageRepository
import org.wycliffeassociates.otter.jvm.persistence.repositories.ProjectRepository
import org.wycliffeassociates.otter.jvm.persistence.repositories.SourceRepository


class CreateProjectUseCase(val languageRepo: LanguageRepository,
                           val sourceRepo: SourceRepository,
                           val collectionRepo: CollectionRepository, val projectRepo: ProjectRepository) {
//    val resourceRepo = resourceRepo

    fun getAllLanguages(): Single<List<Language>> {
        return languageRepo.getAll()
    }

    fun getSourceRepos(): Single<List<Collection>> {
        return sourceRepo.getAllRoot()
    }
    fun getAll(): Single<List<Collection>> {
        return collectionRepo.getAll()
    }

    fun newProject(collection: Collection): Single<Int> {
        return collectionRepo.insert(collection)
    }

    fun getResourceChildren(identifier: SourceCollection): Single<List<Collection>> {
        return sourceRepo.getChildren(identifier)
    }

    fun getAnthologies(): Single<List<Collection>> {
        return projectRepo.getAllRoot()
    }



}