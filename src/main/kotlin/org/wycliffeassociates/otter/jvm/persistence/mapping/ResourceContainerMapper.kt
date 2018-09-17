package org.wycliffeassociates.otter.jvm.persistence.mapping

import io.reactivex.Observable
import jooq.tables.pojos.DublinCoreEntity
import org.wycliffeassociates.otter.common.data.dao.Dao
import org.wycliffeassociates.otter.common.data.mapping.Mapper
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.common.data.model.ResourceContainer

class ResourceContainerMapper(private val languageDao: Dao<Language>) : Mapper<Observable<DublinCoreEntity>, Observable<ResourceContainer>> {
    override fun mapFromEntity(type: Observable<DublinCoreEntity>): Observable<ResourceContainer> {
        return type
                .flatMap { entity ->
                    languageDao
                            .getById(entity.id)
                            .map { language ->
                                ResourceContainer(
                                        entity.id,
                                        entity.conformsto,
                                        entity.creator,
                                        entity.description,
                                        entity.format,
                                        entity.identifier,
                                        entity.issued
                                )
                            }
                }
    }

    override fun mapToEntity(type: Observable<ResourceContainer>): Observable<DublinCoreEntity> {

    }

}