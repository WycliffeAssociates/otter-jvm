package org.wycliffeassociates.otter.jvm.usecases

import io.reactivex.Single
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.jvm.persistence.repositories.ProjectRepository

class ProjectUseCase(val projectRepo : ProjectRepository) {

    fun getAll(): Single<List<Collection>> {
        return projectRepo.getAll()
    }

    fun getAllRoot(): Single<List<Collection>> {
        return projectRepo.getAllRoot()
    }
}