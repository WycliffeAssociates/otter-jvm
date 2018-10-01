package org.wycliffeassociates.otter.jvm.usecases

import io.reactivex.Single
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.jvm.persistence.repo.CollectionRepo
import org.wycliffeassociates.otter.jvm.persistence.repo.LanguagRepo
import org.wycliffeassociates.otter.jvm.persistence.repo.ResourcesRepo


class CreateProjectUseCase(collectionRepo: CollectionRepo,
                           languageRepo: LanguagRepo,
                           resourceRepo: ResourcesRepo) {

    init {
        languageRepo.getAll()
    }
    fun gtLanguages(): Single<List<Language>> {
        return languageRepo.getAll()
    }

}