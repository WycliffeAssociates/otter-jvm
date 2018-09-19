package org.wycliffeassociates.otter.jvm.persistence.mapping

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import jooq.tables.pojos.CollectionEntity
import org.wycliffeassociates.otter.common.data.dao.Dao
import org.wycliffeassociates.otter.common.data.mapping.Mapper
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.ResourceContainer

class CollectionMapper(
        private val resourceContainerDao: Dao<ResourceContainer>
) : Mapper<Observable<CollectionEntity>, Observable<Collection>> {
    override fun mapFromEntity(type: Observable<CollectionEntity>): Observable<Collection> {
        return type
                .flatMap { entity ->
                    resourceContainerDao
                            .getById(entity.rcFk)
                            .map {
                                Collection(
                                        entity.sort,
                                        entity.slug,
                                        entity.label,
                                        entity.title,
                                        it,
                                        entity.id
                                )
                            }
                }
                .subscribeOn(Schedulers.io())

    }

    override fun mapToEntity(type: Observable<Collection>): Observable<CollectionEntity> {
        return type.map {
            CollectionEntity(
                    it.id,
                    null, // filled in by dao when needed
                    null,
                    it.labelKey,
                    it.titleKey,
                    it.slug,
                    it.sort,
                    it.resourceContainer.id
            )
        }
                .subscribeOn(Schedulers.io())
    }

}