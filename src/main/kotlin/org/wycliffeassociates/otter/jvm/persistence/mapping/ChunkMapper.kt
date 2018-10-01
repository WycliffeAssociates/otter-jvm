package org.wycliffeassociates.otter.jvm.persistence.mapping

import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import jooq.tables.daos.ContentDerivativeDao
import jooq.tables.daos.ContentEntityDao
import jooq.tables.pojos.ContentEntity
import org.wycliffeassociates.otter.common.data.model.Chunk
import org.wycliffeassociates.otter.common.persistence.mapping.Mapper
import org.wycliffeassociates.otter.jvm.persistence.repo.TakeDao

class ChunkMapper(
        private val takeDao: TakeDao,
        private val derivedDao: ContentDerivativeDao,
        private val contentEntityDao: ContentEntityDao
) : Mapper<Single<ContentEntity>, Maybe<Chunk>> {
    override fun mapFromEntity(type: Single<ContentEntity>): Maybe<Chunk> {
        return type
                .toMaybe()
                .flatMap { entity ->
                    val chunk = Chunk(
                            entity.sort,
                            entity.label,
                            entity.start,
                            entity.start,
                            // Make call to Take dao to get this, if it exists
                            null,
                            entity.id
                    )
                    // Set the chunk's end
                    chunk.end = derivedDao
                            // Get the derivation table rows for this chunk
                            .fetchByContentFk(chunk.id)
                            // Get the corresponding content from the content table
                            .map { contentEntityDao.fetchOneById(it.sourceFk).start }
                            // Find the largest source start
                            .max() ?: entity.start

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