package org.wycliffeassociates.otter.jvm.persistence

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import org.jooq.DSLContext
import org.wycliffeassociates.otter.common.collections.tree.Tree
import org.wycliffeassociates.otter.common.collections.tree.TreeNode
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Content
import org.wycliffeassociates.otter.common.data.model.ResourceMetadata
import org.wycliffeassociates.otter.common.domain.mapper.mapToMetadata
import org.wycliffeassociates.otter.common.persistence.IRcImporter
import org.wycliffeassociates.otter.common.persistence.repositories.ICollectionRepository
import org.wycliffeassociates.otter.common.persistence.repositories.IContentRepository
import org.wycliffeassociates.otter.jvm.persistence.database.AppDatabase
import org.wycliffeassociates.otter.jvm.persistence.entities.ResourceLinkEntity
import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.CollectionMapper
import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.ContentMapper
import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.LanguageMapper
import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.ResourceMetadataMapper
import org.wycliffeassociates.resourcecontainer.ResourceContainer

class RcImporter(
        private val database: AppDatabase,
        private val collectionRepository: ICollectionRepository,
        private val contentRepository: IContentRepository
) : IRcImporter {

    data class ImportContext(val metadataId: Int, val helpRcTargetMetadata: ResourceMetadata?, val dsl: DSLContext)

    override fun importResourceContainer(rc: ResourceContainer, rcTree: Tree, languageSlug: String): Completable {
        return Completable.fromAction {
            database.transaction { dsl ->
                val language = LanguageMapper().mapFromEntity(database.getLanguageDao().fetchBySlug(languageSlug, dsl))
                val helpRcTargetMetadata: ResourceMetadata? =
                        when (rc.type()) {
                            // TODO: Identifier shouldn't just be set to "ult"
                            "help" -> ResourceMetadataMapper().mapFromEntity(
                                    database.getResourceMetadataDao().fetchByIdentifier("ult", dsl),
                                    language
                            )
                            else -> null
                        }
                val metadata = rc.manifest.dublinCore.mapToMetadata(rc.dir, language)
                val metadataId = database.getResourceMetadataDao().insert(ResourceMetadataMapper().mapToEntity(metadata), dsl)
                val ic = ImportContext(metadataId, helpRcTargetMetadata, dsl)

                importCollection(null, rcTree, ic)
            }
        }.subscribeOn(Schedulers.io())
    }

    private fun importNode(parentId: Int, node: TreeNode, currentCollectionSlug: String, ic: ImportContext) {
        when (node) {
            is Tree -> {
                importCollection(parentId, node, ic)
            }
            is TreeNode -> {
                importContent(parentId, node, currentCollectionSlug, ic)
            }
        }
    }

    private fun importCollection(parentId: Int?, node: Tree, ic: ImportContext) {
        val collection = node.value
        if (collection is Collection) {
            val entity = CollectionMapper().mapToEntity(collection)
            entity.parentFk = parentId
            entity.metadataFk = ic.metadataId
            val id = database.getCollectionDao().insert(entity, ic.dsl)
            for (node in node.children) {
                importNode(id, node, collection.slug, ic)
            }
        }
    }

    private fun importContent(parentId: Int, node: TreeNode, slug: String, ic: ImportContext) {
        val content = node.value
        if (content is Content) {
            val entity = ContentMapper().mapToEntity(content)
            entity.collectionFk = parentId
            val contentId = database.getContentDao().insert(entity, ic.dsl)

            if (ic.helpRcTargetMetadata != null) {
                linkResource(contentId, slug, content.start, ic.helpRcTargetMetadata, ic.dsl)
            }
        }
    }

    private fun linkResource(contentId: Int, slug: String, start: Int, helpRcTargetMetadata: ResourceMetadata, dsl: DSLContext) {
        // TODO: Should I use blocking gets?
        val targetCollection = collectionRepository.getBySlugAndContainer(slug, helpRcTargetMetadata)
                .blockingGet()

        var collectionFk: Int? = null
        var contentFk: Int? = null
        when (start) {
            0 -> collectionFk = targetCollection.id
            else -> contentFk = contentRepository.getByCollectionAndStart(targetCollection, start)
                    .blockingGet()
                    .id
        }
        val resourceEntity = ResourceLinkEntity(
                0,
                contentId,
                contentFk,
                collectionFk
        )
        database.getResourceLinkDao().insert(resourceEntity, dsl)
    }
}
