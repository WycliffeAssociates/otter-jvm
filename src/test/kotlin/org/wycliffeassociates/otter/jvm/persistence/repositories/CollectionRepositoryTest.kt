package org.wycliffeassociates.otter.jvm.persistence.repositories

import com.nhaarman.mockitokotlin2.*
import org.junit.Assert
import org.junit.Test
import org.wycliffeassociates.otter.common.collections.tree.Tree
import org.wycliffeassociates.otter.common.collections.tree.TreeNode
import org.wycliffeassociates.otter.common.data.model.*
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Language
import org.wycliffeassociates.otter.common.domain.mapper.mapToMetadata
import org.wycliffeassociates.otter.common.persistence.IDirectoryProvider
import org.wycliffeassociates.otter.jvm.persistence.database.queries.DeriveProjectQuery
import org.wycliffeassociates.otter.jvm.persistence.entities.ChunkEntity
import org.wycliffeassociates.otter.jvm.persistence.entities.CollectionEntity
import org.wycliffeassociates.otter.jvm.persistence.entities.LanguageEntity
import org.wycliffeassociates.otter.jvm.persistence.entities.ResourceMetadataEntity
import org.wycliffeassociates.otter.jvm.persistence.repositories.mapping.ResourceMetadataMapper
import org.wycliffeassociates.otter.jvm.persistence.repositories.test.MockDatabase
import org.wycliffeassociates.otter.jvm.persistence.resourcecontainer.IResourceContainerIO
import org.wycliffeassociates.resourcecontainer.ResourceContainer
import org.wycliffeassociates.resourcecontainer.entity.*
import java.io.File
import java.lang.RuntimeException
import java.time.LocalDate

class CollectionRepositoryTest {
    private val mockDatabase = MockDatabase.database()
    private val mockDirectoryProvider: IDirectoryProvider = mock {

    }
    private val mockRcIO: IResourceContainerIO = mock {

    }
    private val mockDeriveProjectQuery: DeriveProjectQuery = mock {

    }

    // UUT
    private val collectionRepository = CollectionRepository(
            mockDatabase,
            mockDirectoryProvider,
            rcIO = mockRcIO,
            deriveProjectQuery = mockDeriveProjectQuery
    )

    @Test
    fun shouldCRUDCollection() {
        val collection = create(createMetadata())

        val retrieved = retrieveAll()
        Assert.assertEquals(listOf(collection), retrieved)

        update(collection)
        val retrievedUpdated = retrieveAll()
        Assert.assertEquals(listOf(collection), retrievedUpdated)

        delete(collection)
        val retrievedDeleted = retrieveAll()
        Assert.assertEquals(emptyList<Take>(), retrievedDeleted)
    }

    @Test
    fun shouldHandleDaoFetchExceptionInUpdate() {
        val collection: Collection = mock { on { id } doReturn 0 }
        whenever(mockDatabase.getCollectionDao().fetchById(any(), anyOrNull())).thenThrow(RuntimeException())
        try {
            collectionRepository.update(collection).blockingAwait()
        } catch (e: RuntimeException) {
            Assert.fail("Did not handle DAO exception")
        }
    }

    @Test
    fun shouldUpdateParentGetChildren() {
        val parent = create()
        val collection = create()
        collectionRepository.updateParent(collection, parent).blockingAwait()
        val retrievedChildren = collectionRepository.getChildren(parent).blockingGet()
        Assert.assertEquals(listOf(collection), retrievedChildren)
    }

    @Test
    fun shouldHandleDaoFetchExceptionInUpdateParent() {
        val parent = create()
        val collection: Collection = mock { on { id } doReturn  0 }
        whenever(mockDatabase.getCollectionDao().fetchById(any(), anyOrNull())).thenThrow(java.lang.RuntimeException())
        try {
            collectionRepository.updateParent(collection, parent).blockingAwait()
        } catch (e: RuntimeException) {
            Assert.fail("Did not handle DAO exception")
        }
    }

    @Test
    fun shouldUpdateSource() {
        val source = create()
        val collection = create()
        collectionRepository.updateSource(collection, source).blockingAwait()
        val sourceId = mockDatabase.getCollectionDao().fetchById(collection.id).sourceFk
        Assert.assertEquals(source.id, sourceId)
    }

    @Test
    fun shouldHandleDaoFetchExceptionInUpdateSource() {
        val source = create()
        val collection: Collection = mock { on { id } doReturn  0 }
        whenever(mockDatabase.getCollectionDao().fetchById(any(), anyOrNull())).thenThrow(java.lang.RuntimeException())
        try {
            collectionRepository.updateSource(collection, source).blockingAwait()
        } catch (e: RuntimeException) {
            Assert.fail("Did not handle DAO exception")
        }
    }

    @Test
    fun shouldGetAllCollections() {
        val collections = listOf(create(), create())
        val retrieved = collectionRepository.getAll().blockingGet()

        Assert.assertEquals(collections, retrieved)
    }

    @Test
    fun shouldGetBySlugAndContainer() {
        val collection = create(createMetadata())
        val retrieved = collectionRepository.getBySlugAndContainer(collection.slug, collection.resourceContainer!!)
                .blockingGet()
        Assert.assertEquals(collection, retrieved)
    }

    @Test
    fun shouldHandleGetByInvalidSlugAndContainer() {
        try {
            collectionRepository.getBySlugAndContainer("nonexistent-slug", createMetadata()).blockingGet()
        } catch (e: RuntimeException) {
            Assert.fail("Threw runtime exception")
        }
    }

    @Test
    fun shouldImportTree() {
        /*
         *  Tree Structure
         *  root
         *    |_ child
         *    |    |_ chunk
         *    |    |_ chunk
         *    |
         *    |_ child
         *         |_ deeper child
         *              |_ chunk
         */
        val tree = Tree(create(null, false))
        val children = listOf(Tree(create(null, false)), Tree(create(null, false)))
        val deeperChild = Tree(create(null, false))
        deeperChild.addChild(TreeNode(createChunk(false)))
        children[0].addAll(listOf(TreeNode(createChunk(false)), TreeNode(createChunk(false))))
        children[1].addChild(deeperChild)
        tree.addAll(children)

        val existingLanguage = createLanguage()
        val container: ResourceContainer = ResourceContainer.create(File("./tmp")) {
            manifest = Manifest(
                    dublincore {
                        identifier = "identifier"
                        issued = LocalDate.now().toString()
                        modified = LocalDate.now().toString()
                        language = language {
                            identifier = existingLanguage.slug
                            direction = "ltr"
                            title = "English"
                        }
                        format = "format"
                        subject = "subject"
                        type = "type"
                        title = "title"
                    },
                    listOf(),
                    Checking()
            )
        }

        val expectedMetadata = ResourceMetadataEntity(
                id=1, conformsTo="0.2", creator="", description="",
                format="format", identifier="identifier", issued="2018-11-15",
                languageFk=1, modified="2018-11-15", publisher="", subject="subject",
                type="type", title="title", version="",
                path=container.dir.absolutePath
        )

        val expectedCollections = listOf(
                CollectionEntity(1, null, null, "label", "title", "slug", 1, 1),
                CollectionEntity(2, 1, null, "label", "title", "slug", 1, 1),
                CollectionEntity(3, 1, null, "label", "title", "slug", 1, 1),
                CollectionEntity(4, 3, null, "label", "title", "slug", 1, 1)
        )

        val expectedChunks = listOf(
                ChunkEntity(1, 1, "label", 1, 2, null),
                ChunkEntity(2, 1, "label", 1, 2, null),
                ChunkEntity(3, 1, "label", 1, 4, null)
        )

        collectionRepository.importResourceContainer(container, tree, existingLanguage.slug).blockingAwait()
        // Check if the tree was correctly imported
        Assert.assertEquals(listOf(expectedMetadata), mockDatabase.getResourceMetadataDao().fetchAll())
        Assert.assertEquals(expectedCollections, mockDatabase.getCollectionDao().fetchAll())
        Assert.assertEquals(expectedChunks, mockDatabase.getChunkDao().fetchAll())
    }

    @Test
    fun shouldHandleImportWithIncorrectTypes() {
        /*
         *  Tree Structure
         *  "root"
         *    |_ "child"
         *    |    |_ 1
         *    |    |_ 2
         *    |
         *    |_ "child"
         *         |_ "deeper child"
         *              |_ 3
         */
        val tree = Tree("root")
        val children = listOf(Tree("child"), Tree("child"))
        val deeperChild = Tree("deeper child")
        deeperChild.addChild(TreeNode(3))
        children[0].addAll(listOf(TreeNode(1), TreeNode(2)))
        children[1].addChild(deeperChild)
        tree.addAll(children)

        val existingLanguage = createLanguage()
        val container: ResourceContainer = ResourceContainer.create(File("./tmp")) {
            manifest = Manifest(
                    dublincore {
                        identifier = "identifier"
                        issued = LocalDate.now().toString()
                        modified = LocalDate.now().toString()
                        language = language {
                            identifier = existingLanguage.slug
                            direction = "ltr"
                            title = "English"
                        }
                        format = "format"
                        subject = "subject"
                        type = "type"
                        title = "title"
                    },
                    listOf(),
                    Checking()
            )
        }

        collectionRepository.importResourceContainer(container, tree, existingLanguage.slug).blockingAwait()
        Assert.assertEquals(emptyList<CollectionEntity>(), mockDatabase.getCollectionDao().fetchAll())
        Assert.assertEquals(emptyList<ChunkEntity>(), mockDatabase.getChunkDao().fetchAll())
    }

    // CRUD methods
    private fun create(metadata: ResourceMetadata? = null, insert: Boolean = true): Collection {
        val collection = Collection(
                1,
                "slug",
                "label",
                "title",
                metadata
        )
        if (insert) collection.id = collectionRepository.insert(collection).blockingGet()
        return collection
    }

    private fun retrieveAll(): List<Collection> = collectionRepository.getAll().blockingGet()

    private fun update(
            collection: Collection,
            parent: Collection? = null,
            source: Collection? = null
    ) {
        collection.sort = 2
        collection.slug = "new-slug"
        collection.labelKey = "new-label"
        collection.titleKey = "new-title"
        collection.resourceContainer = createMetadata()
        collectionRepository.update(collection).blockingAwait()
        if (parent != null) collectionRepository.updateParent(collection, parent)
        if (source != null) collectionRepository.updateSource(collection, source)
    }

    private fun delete(collection: Collection) {
        collectionRepository.delete(collection).blockingAwait()
    }

    private fun createMetadata(): ResourceMetadata {
        // Create a dummy selected take
        val metadata = ResourceMetadata(
                "rc0.2",
                "creator",
                "description",
                "format",
                "identifier",
                LocalDate.of(2000, 1, 1),
                createLanguage(),
                LocalDate.of(2018, 2, 4),
                "publisher",
                "subject",
                "type",
                "title",
                "version",
                File("/path/to/rc")
        )
        val metadataEntity = ResourceMetadataEntity(
                0,
                metadata.conformsTo,
                metadata.creator,
                metadata.description,
                metadata.format,
                metadata.identifier,
                metadata.issued.toString(),
                metadata.language.id,
                metadata.modified.toString(),
                metadata.publisher,
                metadata.subject,
                metadata.type,
                metadata.title,
                metadata.version,
                metadata.path.toURI().path
        )
        metadataEntity.id = mockDatabase.getResourceMetadataDao().insert(metadataEntity)
        metadata.id = metadataEntity.id
        return metadata
    }

    private fun createLanguage(): Language {
        val language = Language(
                "en",
                "English",
                "English",
                "ltr",
                true
        )
        val languageEntity = LanguageEntity(
                0,
                language.slug,
                language.name,
                language.anglicizedName,
                language.direction,
                if (language.isGateway) 1 else 0
        )
        languageEntity.id = mockDatabase.getLanguageDao().insert(languageEntity)
        language.id = languageEntity.id
        return language
    }

    private fun createChunk(insert: Boolean = true): Chunk {
        val chunk = Chunk(
                1,
                "label",
                1,
                1,
                null
        )
        val chunkEntity = ChunkEntity(
                0,
                chunk.sort,
                chunk.labelKey,
                chunk.start,
                chunk.end,
                null
        )
        if (insert) chunkEntity.id = mockDatabase.getChunkDao().insert(chunkEntity)
        chunk.id = chunkEntity.id
        return chunk
    }
}