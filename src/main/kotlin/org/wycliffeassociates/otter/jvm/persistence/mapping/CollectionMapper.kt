package org.wycliffeassociates.otter.jvm.persistence.mapping

import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import jooq.tables.pojos.CollectionEntity
import org.wycliffeassociates.otter.common.data.mapping.Mapper
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.jvm.persistence.repo.ResourceContainerDao

class CollectionMapper(
        private val resourceContainerDao: ResourceContainerDao
) : Mapper<Single<CollectionEntity>, Maybe<Collection>> {
    override fun mapFromEntity(type: Single<CollectionEntity>): Maybe<Collection> {
        return type
                .toMaybe()
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

    override fun mapToEntity(type: Maybe<Collection>): Single<CollectionEntity> {
        return type
                .toSingle()
                .map {
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