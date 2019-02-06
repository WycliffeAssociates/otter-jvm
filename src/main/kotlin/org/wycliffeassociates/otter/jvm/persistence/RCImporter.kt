package org.wycliffeassociates.otter.jvm.persistence

import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import org.jooq.DSLContext
import org.wycliffeassociates.otter.common.collections.tree.Tree
import org.wycliffeassociates.otter.common.collections.tree.TreeNode
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Content
import org.wycliffeassociates.otter.common.domain.mapper.mapToMetadata
import org.wycliffeassociates.otter.common.persistence.IRcImporter
import org.wycliffeassociates.otter.common.persistence.repositories.ICollectionRepository
import org.wycliffeassociates.otter.common.persistence.repositories.IContentRepository
import org.wycliffeassociates.otter.jvm.persistence.database.AppDatabase
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

    override fun importResourceContainer(rc: ResourceContainer, rcTree: Tree, languageSlug: String): Completable {
        return Completable.fromAction {
            database.transaction { dsl ->
                val language = LanguageMapper().mapFromEntity(database.getLanguageDao().fetchBySlug(languageSlug, dsl))
                val metadata = rc.manifest.dublinCore.mapToMetadata(rc.dir, language)
                val metadataId = database.getResourceMetadataDao().insert(ResourceMetadataMapper().mapToEntity(metadata), dsl)

                importCollection(null, metadataId, rcTree, dsl)
            }
        }.subscribeOn(Schedulers.io())
    }

    private fun importNode(parentId: Int, metadataId: Int, node: TreeNode, dsl: DSLContext) {
        when(node) {
            is Tree -> {
                importCollection(parentId, metadataId, node, dsl)
            }
            is TreeNode -> {
                importContent(parentId, node, dsl)
            }
        }
    }

    private fun importCollection(parentId: Int?, metadataId: Int, node: Tree, dsl: DSLContext){
        val collection = node.value
        if (collection is Collection) {
            val entity = CollectionMapper().mapToEntity(collection)
            entity.parentFk = parentId
            entity.metadataFk = metadataId
            val id = database.getCollectionDao().insert(entity, dsl)
            for (node in node.children) {
                importNode(id, metadataId, node, dsl)
            }
        }
    }

    private fun importContent(parentId: Int, node: TreeNode, dsl: DSLContext) {
        val content = node.value
        if (content is Content) {
            val entity = ContentMapper().mapToEntity(content)
            entity.collectionFk = parentId
            database.getContentDao().insert(entity, dsl)
        }
    }
}
