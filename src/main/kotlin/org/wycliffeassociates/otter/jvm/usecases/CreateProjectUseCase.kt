package org.wycliffeassociates.otter.jvm.usecases

import io.reactivex.Single
import io.reactivex.rxjavafx.schedulers.JavaFxScheduler
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.common.data.model.Resource
import org.wycliffeassociates.otter.jvm.persistence.repositories.CollectionRepository
import org.wycliffeassociates.otter.jvm.persistence.repositories.LanguageRepository
import org.wycliffeassociates.otter.jvm.persistence.repositories.ProjectRepository
import org.wycliffeassociates.otter.jvm.persistence.repositories.SourceRepository


class CreateProjectUseCase(val languageRepo: LanguageRepository, val sourceRepo: SourceRepository, val collectionRepo: CollectionRepository) {
//    val resourceRepo = resourceRepo

    fun getAllLanguages(): Single<List<Language>> {
        return languageRepo.getAll()
    }

    fun getSourceRepos(): Single<List<Collection>> {
//        println(collectionRepo.getAll().observeOn(JavaFxScheduler.platform()).map { println(it.toString() + "in Use case") }.subscribe())
        return sourceRepo.getAllRoot()
    }

//    fun getAnthologies(): Single<List<Collection>> {
//        return
//    }

    fun getAll(): Single<List<Collection>> {
        return collectionRepo.getAll()
    }

//    fun getAllProject() : Single<List<Collection>> {
//        return collectionRepo.getAll()
//    }

//    fun getAllResources(): Single<List<Resource>> {
//        return resourceRepo.getAll()
//    }

//    fun getSources(): Single<List<Collection>> {
//        return sourceRepository.getAll()
//    }

}