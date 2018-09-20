package org.wycliffeassociates.otter.jvm.persistence.mapping

import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import jooq.tables.pojos.ContentEntity
import org.wycliffeassociates.otter.common.data.mapping.Mapper
import org.wycliffeassociates.otter.common.data.model.Chunk
import org.wycliffeassociates.otter.jvm.persistence.repo.TakeDao

class ChunkMapper(private val takeDao: TakeDao) : Mapper<Single<ContentEntity>, Maybe<Chunk>> {
    override fun mapFromEntity(type: Single<ContentEntity>): Maybe<Chunk> {
        return type
                .toMaybe()
                .flatMap { entity ->
                    val chunk = Chunk(
                            entity.sort,
                            entity.label,
                            entity.start,
                            // end: make a call to derived dao to figure out max derived verse
                            // if not derived, then same as start
                            entity.start,
                            // Make call to Take dao to get this, if it exists
                            null,
                            entity.id
                    )
                    if (entity.selectedTakeFk != null) {
                        // Selected take exists
                        takeDao
                                .getById(entity.selectedTakeFk)
                                .map {
                                    chunk.selectedTake = it
                                    chunk
                                }
                    } else {
                        Maybe.just(chunk)
                    }
                }
                .subscribeOn(Schedulers.io())
    }

    override fun mapToEntity(type: Maybe<Chunk>): Single<ContentEntity> {
        return type
                .toSingle()
                .map {
                    ContentEntity(
                            it.id,
                            null, // Leave null to be filled in by dao
                            it.labelKey,
                            it.selectedTake?.id,
                            it.start,
                            it.sort
                    )
                }
                .subscribeOn(Schedulers.io())
    }

}