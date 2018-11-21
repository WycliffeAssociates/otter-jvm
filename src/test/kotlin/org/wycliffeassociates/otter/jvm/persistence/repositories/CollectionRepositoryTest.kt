package org.wycliffeassociates.otter.jvm.persistence.repositories

import com.nhaarman.mockitokotlin2.*
import org.junit.Assert
import org.junit.Test
import org.mockito.stubbing.Answer
import org.wycliffeassociates.otter.common.collections.tree.Tree
import org.wycliffeassociates.otter.common.collections.tree.TreeNode
import org.wycliffeassociates.otter.common.data.model.*
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.persistence.IDirectoryProvider
import org.wycliffeassociates.otter.jvm.persistence.database.daos.CollectionDao
import org.wycliffeassociates.otter.jvm.persistence.database.queries.DeriveProjectQuery
import org.wycliffeassociates.otter.jvm.persistence.entities.ChunkEntity
import org.wycliffeassociates.otter.jvm.persistence.entities.CollectionEntity
import org.wycliffeassociates.otter.jvm.persistence.entities.LanguageEntity
import org.wycliffeassociates.otter.jvm.persistence.entities.ResourceMetadataEntity
import org.wycliffeassociates.otter.jvm.persistence.repositories.test.MockDatabase
import org.wycliffeassociates.otter.jvm.persistence.repositories.test.MockResourceContainerIO
import org.wycliffeassociates.resourcecontainer.ResourceContainer
import org.wycliffeassociates.resourcecontainer.entity.Checking
import org.wycliffeassociates.resourcecontainer.entity.Manifest
import org.wycliffeassociates.resourcecontainer.entity.dublincore
import org.wycliffeassociates.resourcecontainer.entity.language
import java.io.File
import java.time.LocalDate

class CollectionRepositoryTest {
    private val mockDatabase = MockDatabase.database()
    private val mockDirectoryProvider: IDirectoryProvider = mock {

    }
    private val mockRcIO = MockResourceContainerIO()
    private val mockDeriveProjectQuery: DeriveProjectQuery = mock()

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
    fun shouldUpdateParentGetChildren() {
        val parent = create()
        val collection = create()
        collectionRepository.updateParent(collection, parent).blockingAwait()
        val retrievedChildren = collectionRepository.getChildren(parent).blockingGet()
        Assert.assertEquals(listOf(collection), retrievedChildren)
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
    fun shouldGetRootProjects() {
        val sourceEntity = CollectionEntity(0, null, null, "project", "source", "src", 0, null)
        val sourceChapterEntity = CollectionEntity(0, 1, null, "chapter", "source chapter", "chap", 0, null)
        val projectEntity = CollectionEntity(0, null, 1, "project", "derived", "der", 0, null)
        val projectChapterEntity = CollectionEntity(0, 3, 2, "chapter", "project chapter", "chap", 0, null)
        val expectedCollection = Collection(0, "der", "project", "derived", null, 3)

        mockDatabase.getCollectionDao().insert(sourceEntity)
        mockDatabase.getCollectionDao().insert(sourceChapterEntity)
        mockDatabase.getCollectionDao().insert(projectEntity)
        mockDatabase.getCollectionDao().insert(projectChapterEntity)


        val retrieved = collectionRepository.getRootProjects().blockingGet()
        Assert.assertEquals(listOf(expectedCollection), retrieved)
    }

    @Test
    fun shouldGetRootSources() {
        val sourceEntity = CollectionEntity(0, null, null, "root", "source", "src", 0, null)
        val sourceChapterEntity = CollectionEntity(0, 1, null, "chapter", "source chapter", "chap", 0, null)
        val projectEntity = CollectionEntity(0, null, 1, "project", "derived", "der", 0, null)
        val projectChapterEntity = CollectionEntity(0, 3, 2, "chapter", "project chapter", "chap", 0, null)
        val expectedCollection = Collection(0, "src", "root", "source", null, 1)

        mockDatabase.getCollectionDao().insert(sourceEntity)
        mockDatabase.getCollectionDao().insert(sourceChapterEntity)
        mockDatabase.getCollectionDao().insert(projectEntity)
        mockDatabase.getCollectionDao().insert(projectChapterEntity)


        val retrieved = collectionRepository.getRootSources().blockingGet()
        Assert.assertEquals(listOf(expectedCollection), retrieved)
    }

    @Test
    fun shouldGetSource() {
        val sourceEntity = CollectionEntity(0, null, null, "source", "source", "src", 0, null)
        val projectEntity = CollectionEntity(0, null, 1, "project", "derived", "der", 0, null)
        val projectCollection = Collection(0, "der", "project", "derived", null, 2)
        val expectedCollection = Collection(0, "src", "source", "source", null, 1)

        mockDatabase.getCollectionDao().insert(sourceEntity)
        mockDatabase.getCollectionDao().insert(projectEntity)


        val retrieved = collectionRepository.getSource(projectCollection).blockingGet()
        Assert.assertEquals(expectedCollection, retrieved)
    }

    @Test
    fun shouldHandleDaoExceptionInGetSource() {
        val mockExceptionDao: CollectionDao = mock(defaultAnswer = Answer<Any> { throw RuntimeException() })
        whenever(mockDatabase.getCollectionDao()).doReturn(mockExceptionDao)
        try {
            collectionRepository.getSource(mock()).blockingGet()
        } catch (e: RuntimeException) {
            Assert.fail("Did not handle DAO exception")
        }
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
        val tree = Tree(create(null, 1, false))
        val children = listOf(Tree(create(null, 1, false)), Tree(create(null, 1, false)))
        val deeperChild = Tree(create(null, 1, false))
        deeperChild.addChild(TreeNode(createChunk(false)))
        children[0].addAll(listOf(TreeNode(createChunk(false)), TreeNode(createChunk(false))))
        children[1].addChild(deeperChild)
        tree.addAll(children)

        val existingLanguage = createLanguage()
        val container: ResourceContainer = ResourceContainer.create(File("./tmp")) {
            manifest = Manifest(
                    dublincore {
                        identifier = "identifier"
                        issued = "2018-11-15"
                        modified = "2018-11-18"
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
                languageFk=1, modified="2018-11-18", publisher="", subject="subject",
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

    @Test
    fun shouldDeriveProject() {
        val sourceMetadata = createMetadata()
        val sourceMetadataEntity = mockDatabase.getResourceMetadataDao().fetchById(1)
        val source = create(sourceMetadata, 40)
        val sourceEntity = mockDatabase.getCollectionDao().fetchById(1)
        val targetLanguage = createLanguage("ar", "Arabic")

        // Based on the mock resource container IO
        val expectedMetadata = ResourceMetadataEntity(
                2,
                "0.2",
                "",
                "",
                "text/usfm",
                sourceMetadata.identifier,
                LocalDate.now().toString(),
                targetLanguage.id,
                LocalDate.now().toString(),
                "",
                sourceMetadata.subject,
                "book",
                sourceMetadata.title,
                "",
                File("./rc").absolutePath
        )
        val expectedDerived = CollectionEntity(
                2,
                null,
                source.id,
                source.labelKey,
                source.titleKey,
                source.slug,
                source.sort,
                expectedMetadata.id
        )
        collectionRepository.deriveProject(source, targetLanguage).blockingAwait()

        verify(mockDeriveProjectQuery).execute(eq(source.id), eq(2), eq(2), anyOrNull())
        verify(mockRcIO.container).write()

        Assert.assertEquals(listOf(sourceEntity, expectedDerived), mockDatabase.getCollectionDao().fetchAll())
        Assert.assertEquals(listOf(sourceMetadataEntity, expectedMetadata), mockDatabase.getResourceMetadataDao().fetchAll())
        Assert.assertEquals(sourceEntity.sort, mockRcIO.container.manifest.projects[0].sort)
    }

    @Test
    fun shouldDeriveProjectWithExistingMetadata() {
        val sourceMetadata = createMetadata()
        val sourceMetadataEntity = mockDatabase.getResourceMetadataDao().fetchById(1)
        val source = create(sourceMetadata, 40)
        val sourceEntity = mockDatabase.getCollectionDao().fetchById(1)
        val targetLanguage = createLanguage("ar", "Arabic")

        // Based on the mock resource container IO
        val existingMetadata = ResourceMetadataEntity(
                2,
                "0.2",
                "",
                "",
                "text/usfm",
                sourceMetadata.identifier,
                LocalDate.now().toString(),
                targetLanguage.id,
                LocalDate.now().toString(),
                "",
                sourceMetadata.subject,
                "book",
                sourceMetadata.title,
                "",
                File("./rc").absolutePath
        )
        mockDatabase.getResourceMetadataDao().insert(existingMetadata)
        val expectedDerived = CollectionEntity(
                2,
                null,
                source.id,
                source.labelKey,
                source.titleKey,
                source.slug,
                source.sort,
                existingMetadata.id
        )
        collectionRepository.deriveProject(source, targetLanguage).blockingAwait()

        verify(mockDeriveProjectQuery).execute(eq(source.id), eq(2), eq(2), anyOrNull())
        verify(mockRcIO.container).write()

        Assert.assertEquals(listOf(sourceEntity, expectedDerived), mockDatabase.getCollectionDao().fetchAll())
        Assert.assertEquals(listOf(sourceMetadataEntity, existingMetadata), mockDatabase.getResourceMetadataDao().fetchAll())
        Assert.assertEquals(sourceEntity.sort, mockRcIO.container.manifest.projects[0].sort)
    }

    @Test
    fun shouldIncrementSortIfBibleNTProject() {
        val sourceMetadata = createMetadata("Bible")
        val source = create(sourceMetadata, 40)
        val sourceEntity = mockDatabase.getCollectionDao().fetchById(1)
        val targetLanguage = createLanguage("ar", "Arabic")

        // Based on the mock resource container IO
        val expectedDerived = CollectionEntity(
                2,
                null,
                source.id,
                source.labelKey,
                source.titleKey,
                source.slug,
                source.sort,
                2
        )
        collectionRepository.deriveProject(source, targetLanguage).blockingAwait()

        Assert.assertEquals(listOf(sourceEntity, expectedDerived), mockDatabase.getCollectionDao().fetchAll())
        Assert.assertEquals(sourceEntity.sort + 1, mockRcIO.container.manifest.projects[0].sort)
    }

    @Test
    fun shouldNotIncrementSortIfBibleOTProject() {
        val sourceMetadata = createMetadata("Bible")
        val source = create(sourceMetadata, 39)
        val sourceEntity = mockDatabase.getCollectionDao().fetchById(1)
        val targetLanguage = createLanguage("ar", "Arabic")

        // Based on the mock resource container IO
        val expectedDerived = CollectionEntity(
                2,
                null,
                source.id,
                source.labelKey,
                source.titleKey,
                source.slug,
                source.sort,
                2
        )
        collectionRepository.deriveProject(source, targetLanguage).blockingAwait()

        Assert.assertEquals(listOf(sourceEntity, expectedDerived), mockDatabase.getCollectionDao().fetchAll())
        Assert.assertEquals(sourceEntity.sort, mockRcIO.container.manifest.projects[0].sort)
    }

    /* TODO: Test if project already exists in manifest? */

    // CRUD methods
    private fun create(metadata: ResourceMetadata? = null, sort: Int = 1, insert: Boolean = true): Collection {
        val collection = Collection(
                sort,
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

    private fun createMetadata(subject: String = "subject"): ResourceMetadata {
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
                subject,
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

    private fun createLanguage(slug: String = "en", name: String = "English"): Language {
        val language = Language(
                slug,
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