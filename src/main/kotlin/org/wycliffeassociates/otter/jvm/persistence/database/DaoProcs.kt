package org.wycliffeassociates.otter.jvm.persistence.database

import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.wycliffeassociates.otter.common.data.model.RelatedCollectionContent
import org.wycliffeassociates.otter.jvm.persistence.database.daos.ChunkDao
import org.wycliffeassociates.otter.jvm.persistence.database.daos.CollectionDao
import org.wycliffeassociates.otter.jvm.persistence.entities.ChunkEntity
import org.wycliffeassociates.otter.jvm.persistence.entities.CollectionEntity
import org.wycliffeassociates.otter.jvm.persistence.entities.ResourceMetadataEntity

class DaoProcs(
    private val dsl: DSLContext
) {
    private fun _recursiveDuplicateCollectionAndContent(
            sourceCollection: CollectionEntity,
            parentCollection: CollectionEntity?,
            metadata: ResourceMetadataEntity,
            collectionDao: CollectionDao,
            chunkDao: ChunkDao,
            depthLimit: Int = Int.MAX_VALUE
    ): CollectionEntity {
        // Duplicate and insert the collection
        val derivedCollection = sourceCollection.copy(
                id = 0,
                sourceFk = sourceCollection.id,
                parentFk = parentCollection?.id,
                metadataFk = metadata.id
        )
        derivedCollection.id = collectionDao.insert(derivedCollection)

        // Duplicate any content tied to this collection
        for (chunk in chunkDao.fetchByCollectionId(sourceCollection.id)) {
            val derivedChunk = chunk.copy(
                    id = 0,
                    collectionFk = derivedCollection.id,
                    selectedTakeFk = null
            )
            derivedChunk.id = chunkDao.insert(derivedChunk)
            chunkDao.updateSources(derivedChunk, listOf(chunk))
        }

        // Duplicate any subcollections
        if (depthLimit > 0) {
            for (subcollection in collectionDao.fetchChildren(sourceCollection)) {
                _recursiveDuplicateCollectionAndContent(
                        subcollection,
                        derivedCollection,
                        metadata,
                        collectionDao,
                        chunkDao,
                        depthLimit - 1
                )
            }
        }

        return derivedCollection
    }

    // Duplicates a source collection
    fun recursiveDuplicateCollectionAndContent(
            sourceEntity: CollectionEntity,
            newMetadata: ResourceMetadataEntity,
            depthLimit: Int = Int.MAX_VALUE
    ): CollectionEntity {
        var newRoot: CollectionEntity? = null
        // Start a transaction
        dsl.transaction {  config ->
            val localDsl = DSL.using(config)

            // Create the local daos
            val collectionDao = CollectionDao(localDsl)
            val chunkDao = ChunkDao(localDsl)

            // Recursively duplicate within this transaction
            // Call the private function
            newRoot = _recursiveDuplicateCollectionAndContent(
                    sourceEntity,
                    null,
                    newMetadata,
                    collectionDao,
                    chunkDao,
                    depthLimit
            )
        }
        // Throw an error if no new root
        if (newRoot == null) throw NullPointerException("Duplicate collection was not created")
        return newRoot!!
    }

    // Assumes metadata has been inserted already
    fun insertAllCollectionsChunks(
            collections: List<CollectionEntity>,
            chunks: List<ChunkEntity>
    ) {
        // Start a transaction
        dsl.transaction { config ->
            val localDsl = DSL.using(config)

            val collectionDao = CollectionDao(localDsl)
            val chunkDao = ChunkDao(localDsl)

            for (collection in collections) {
                collectionDao.insert(collection)
            }

            for (chunk in chunks) {
                chunkDao.insert(chunk)
            }
        }
    }
}