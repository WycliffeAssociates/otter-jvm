package org.wycliffeassociates.otter.jvm.persistence.mapping

import io.reactivex.Observable
import jooq.tables.pojos.ContentEntity
import org.wycliffeassociates.otter.common.data.mapping.Mapper
import org.wycliffeassociates.otter.common.data.model.Chunk

class ChunkMapper : Mapper<Observable<ContentEntity>, Observable<Chunk>> {
    override fun mapFromEntity(type: Observable<ContentEntity>): Observable<Chunk> {
        return type
                .map {
                    Chunk(
                            it.id,
                            it.sort,
                            it.label,
                            it.start,
                            // end: make a call to derived dao to figure out max derived verse
                            // if not derived, then same as start
                            it.start,
                            // Make call to Take dao to get this, if it exists
                            null
                    )
                }
    }

    override fun mapToEntity(type: Observable<Chunk>): Observable<ContentEntity> {
        return type
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
    }

}