package org.wycliffeassociates.otter.jvm.usecases

import io.reactivex.Single
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.common.data.model.Resource
import org.wycliffeassociates.otter.jvm.persistence.repositories.CollectionRepository
import org.wycliffeassociates.otter.jvm.persistence.repositories.LanguageRepository
import org.wycliffeassociates.otter.jvm.persistence.repositories.SourceRepository


class CreateProjectUseCase(val languageRepo: LanguageRepository
                           ) {
//    val resourceRepo = resourceRepo

    fun getAllLanguages(): Single<List<Language>> {
        return languageRepo.getAll()
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