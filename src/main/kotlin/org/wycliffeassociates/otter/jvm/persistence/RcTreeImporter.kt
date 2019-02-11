package org.wycliffeassociates.otter.jvm.persistence

import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.schedulers.Schedulers
import org.jooq.DSLContext
import org.wycliffeassociates.otter.common.collections.tree.Tree
import org.wycliffeassociates.otter.common.collections.tree.TreeNode
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Content
import org.wycliffeassociates.otter.common.data.model.ResourceMetadata
import org.wycliffeassociates.otter.common.domain.mapper.mapToMetadata
import org.wycliffeassociates.otter.common.persistence.IRcTreeImporter
import org.wycliffeassociates.otter.common.persistence.repositories.ICollectionRepository
import org.wycliffeassociates.otter.common.persistence.repositories.IContentRepository
import org.wycliffeassociates.otter.jvm.persistence.database.AppDatabase
import org.wycliffeassociates.otter.jvm.persistence.entities.ResourceLinkEntity
import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.CollectionMapper
import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.ContentMapper
import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.LanguageMapper
import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.ResourceMetadataMapper
import org.wycliffeassociates.resourcecontainer.ResourceContainer

class RcTreeImporter(
        private val database: AppDatabase,
        private val collectionRepository: ICollectionRepository,
        private val contentRepository: IContentRepository
) : IRcTreeImporter {

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
                val dublinCoreFk = database.getResourceMetadataDao().insert(ResourceMetadataMapper().mapToEntity(metadata), dsl)
                val ih = ImportHelper(dublinCoreFk, helpRcTargetMetadata, dsl)

                ih.importCollection(null, rcTree)
            }
        }.subscribeOn(Schedulers.io())
    }

    inner class ImportHelper(val dublinCoreId: Int, val helpRcTargetMetadata: ResourceMetadata?, val dsl: DSLContext) {

        fun importCollection(parentId: Int?, node: Tree) =
                (node.value as? Collection)?.let { collection ->
                    when (helpRcTargetMetadata) {
                        null -> addCollection(collection, parentId)
                        else -> findCollectionId(collection, helpRcTargetMetadata)
                    }
                }?.subscribe { id ->
                    for (childNode in node.children) {
                        importNode(id, childNode)
                    }
                }

        private fun findCollectionId(collection: Collection, helpRcTargetMetadata: ResourceMetadata): Maybe<Int> =
                collectionRepository
                        .getBySlugAndContainer(collection.slug, helpRcTargetMetadata)
                        .map(Collection::id)

        private fun addCollection(collection: Collection, parentId: Int?): Maybe<Int> =
                Maybe.fromCallable {
                    val entity = CollectionMapper().mapToEntity(collection).apply {
                        parentFk = parentId
                        dublinCoreFk = dublinCoreId
                    }
                    database.getCollectionDao().insert(entity, dsl)
                }

        private fun importNode(parentId: Int, node: TreeNode) {
            when (node) {
                is Tree -> {
                    importCollection(parentId, node)
                }
                is TreeNode -> {
                    importContent(parentId, node)
                }
            }
        }

        private fun importContent(parentId: Int, node: TreeNode) {
            val content = node.value
            if (content is Content) {
                val entity = ContentMapper().mapToEntity(content).apply { collectionFk = parentId }
                val contentId = database.getContentDao().insert(entity, dsl)
                if (helpRcTargetMetadata != null) {
                    linkResource(parentId, contentId, content.start)
                }
            }
        }

        private fun linkResource(parentId: Int, contentId: Int, start: Int) {
            when (start) {
                0 -> addResource(contentId, null, parentId) // chapter resource
                else -> contentRepository.getByCollectionIdAndStart(parentId, start) // verse resource
                        .doOnSuccess { addResource(contentId, it.id, null) }
                        .doOnError { throw it }
            }
        }

        private fun addResource(contentId: Int, contentFk: Int?, collectionFk: Int?) {
            val resourceEntity = ResourceLinkEntity(
                    0,
                    contentId,
                    contentFk,
                    collectionFk,
                    dublinCoreId
            )
            database.getResourceLinkDao().insert(resourceEntity, dsl)
        }
    }
}
