package org.wycliffeassociates.otter.jvm.persistence.repositories

import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.ReplayRelay
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Content
import org.wycliffeassociates.otter.common.data.model.MimeType
import org.wycliffeassociates.otter.common.data.workbook.*
import org.wycliffeassociates.otter.common.persistence.repositories.IWorkbookRepository
import java.util.*

class WorkbookRepository(
    private val collectionRepository: CollectionRepository,
    private val contentRepository: ContentRepository,
    private val resourceRepository: ResourceRepository,
    private val takeRepository: TakeRepository
) : IWorkbookRepository {
    /** Disposers for Relays in the current workbook. */
    private val connections = CompositeDisposable()

    override fun get(source: Collection, target: Collection): Workbook {
        // Clear database connections and dispose observables for the
        // previous Workbook if a new one was requested.
        connections.clear()
        return Workbook(book(source), book(target))
    }

    private fun book(bookCollection: Collection): Book {
        return Book(
            title = bookCollection.titleKey,
            sort = bookCollection.sort,
            hasResources = false, // TODO (count resources where resource to content / collection is in book, chapters, or chunks)
            chapters = constructBookChapters(bookCollection)
        )
    }

    private fun constructBookChapters(bookCollection: Collection): Observable<Chapter> {
        val collections = collectionRepository
            .getChildren(bookCollection)
            .flattenAsObservable { list -> list.sortedBy { it.sort } }

        val chapters = collections
            .concatMapEager { constructChapter(it).toObservable() }

        return chapters.cache()
    }

    private fun constructChapter(chapterCollection: Collection): Single<Chapter> {
        return contentRepository
            .getCollectionMetaContent(chapterCollection)
            .map { metaContent ->
                Chapter(
                    title = chapterCollection.titleKey,
                    sort = chapterCollection.sort,
                    hasResources = false, // TODO
                    resources = constructResources(chapterCollection),
                    audio = constructAssociatedAudio(metaContent),
                    chunks = constructChunks(chapterCollection)
                )
            }
    }

    private fun constructChunks(chapterCollection: Collection): Observable<Chunk> {
        val contents = contentRepository
            .getByCollection(chapterCollection)
            .flattenAsObservable { list -> list.sortedBy { it.sort } }
            .filter { it.labelKey != "constructChapter" } // TODO: filter by something better

        val chunks = contents
            .map {
                Chunk(
                    title = it.start.toString(), // ???
                    sort = it.sort,
                    audio = constructAssociatedAudio(it),
                    hasResources = false, // TODO
                    resources = constructResources(it),
                    text = textItem(it)
                )
            }

        return chunks.cache()
    }

    private fun textItem(content: Content?): TextItem? {
        return content
            ?.format
            ?.let { MimeType.of(it) }
            ?.let { mimeType ->
                content.text?.let {
                    TextItem(it, mimeType)
                }
            }
    }

    private fun constructResource(title: Content, body: Content?): Resource? {
        val titleTextItem = textItem(title)
            ?: return null

        return Resource(
            sort = title.sort,
            title = titleTextItem,
            body = textItem(body),
            titleAudio = constructAssociatedAudio(title),
            bodyAudio = body?.let { constructAssociatedAudio(body) }
        )
    }

    private fun constructResources(content: Content): Observable<Resource> {
        return resourceRepository
            .getByContent(content)
            .flattenAsObservable(this::contentsToResources)
            .cache()
    }

    private fun constructResources(collection: Collection): Observable<Resource> {
        return resourceRepository
            .getByCollection(collection)
            .flattenAsObservable(this::contentsToResources)
            .cache()
    }

    private fun contentsToResources(contents: Iterable<Content>): Iterable<Resource> {
        return contents
            .sortedWith(compareBy({ it.start }, { it.sort }))
            .zipWithNext { a, b ->
                when {
                    a.labelKey != "title" -> null
                    b.labelKey != "body" -> constructResource(a, null)
                    else -> constructResource(a, b)
                }
            }
            .filterNotNull()
    }

    private fun workbookTake(modelTake: org.wycliffeassociates.otter.common.data.model.Take): Take {
        // TODO: move into TakeMapper?
        return Take(
            name = modelTake.filename,
            file = modelTake.path,
            number = modelTake.number,
            format = MimeType.WAV, // TODO
            createdTimestamp = modelTake.timestamp,
            deletedTimestamp = BehaviorRelay.createDefault(null) // TODO: need DB deleted field
        )
    }

    private fun modelTake(workbookTake: Take): org.wycliffeassociates.otter.common.data.model.Take {
        // TODO: move into TakeMapper?
        return org.wycliffeassociates.otter.common.data.model.Take(
            filename = workbookTake.file.name,
            path = workbookTake.file,
            number = workbookTake.number,
            timestamp = workbookTake.createdTimestamp,
            played = false,
            markers = listOf()
        )
    }

    private fun constructAssociatedAudio(content: Content): AssociatedAudio {
        /** Map to recover model.Take objects from workbook.Take objects. */
        val takeMap = WeakHashMap<Take, org.wycliffeassociates.otter.common.data.model.Take>()

        /** Initial Takes read from the DB. */
        val takesFromDb = takeRepository
            .getByContent(content)
            .flattenAsObservable { list -> list.sortedBy { it.number } }
            .map { workbookTake(it) to it }

        /** Relay to send Takes out to consumers, but also receive new Takes from UI. */
        val takesRelay = ReplayRelay.create<Take>()
        takesFromDb
            // Record the mapping between data types.
            .doOnNext { (wbTake, modelTake) -> takeMap[wbTake] = modelTake }
            // Feed the initial list to takesRelay
            .map { (wbTake, _) -> wbTake }
            .subscribe(takesRelay)

        /** When we receive new takes, update the map and write to the DB. */
        val takesRelaySubscription = takesRelay
            // Skip Takes we have seen before.
            .filter { !takeMap.contains(it) }
            // Make a Pair<workbook.Take, model.Take>
            .map { it to modelTake(it) }
            // Keep the takeMap current.
            .doOnNext { (wbTake, modelTake) -> takeMap[wbTake] = modelTake }
            // Insert the new take into the DB.
            .subscribe { (_, modelTake) ->
                takeRepository.insertForContent(modelTake, content)
            }

        /** The initial selected take, from the DB. */
        val initialSelectedTake = content.selectedTake?.let { workbookTake(it) }

        /** Relay to send selected-take updates out to consumers, but also receive updates from UI. */
        val selectedTakeRelay = BehaviorRelay.createDefault(initialSelectedTake)

        /** When we receive an update, write it to the DB. */
        val selectedTakeRelaySubscription = selectedTakeRelay
            .distinctUntilChanged() // Don't write unless changed
            .skip(1) // Don't write the value we just loaded from the DB
            .map { takeMap[it] }
            .subscribe {
                content.selectedTake = it
                contentRepository.update(content)
            }

        connections += takesRelaySubscription
        connections += selectedTakeRelaySubscription
        return AssociatedAudio(takesRelay, selectedTakeRelay)
    }

}
