package org.wycliffeassociates.otter.jvm.persistence.repositories.mapping

import org.wycliffeassociates.otter.common.data.model.RelatedCollection
import org.wycliffeassociates.otter.jvm.persistence.database.DaoProcs

class RelatedCollectionMapper(
        private val collectionMapper: CollectionMapper,
        private val chunkMapper: ChunkMapper
) {
    fun mapToEntity(related: RelatedCollection): DaoProcs.RelatedCollectionEntity {
        return DaoProcs.RelatedCollectionEntity(
                collectionMapper.mapToEntity(related.collection),
                related.subcollections.map(::mapToEntity),
                related.content.map(chunkMapper::mapToEntity)
        )
    }
}