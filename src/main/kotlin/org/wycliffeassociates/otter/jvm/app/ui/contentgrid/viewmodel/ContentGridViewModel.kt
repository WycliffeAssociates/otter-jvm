package org.wycliffeassociates.otter.jvm.app.ui.contentgrid.viewmodel

import com.github.thomasnield.rxkotlinfx.changes
import com.github.thomasnield.rxkotlinfx.observeOnFx
import com.github.thomasnield.rxkotlinfx.toObservable
import io.reactivex.Observable
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Content
import org.wycliffeassociates.otter.common.data.model.ResourceMetadata
import org.wycliffeassociates.otter.common.domain.content.AccessTakes
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import tornadofx.*
import java.util.concurrent.TimeUnit
import java.util.function.Predicate

data class ContentInfo(
        val content: SimpleObjectProperty<Content>,
        val hasTake: SimpleBooleanProperty
)

class ContentGridViewModel: ViewModel() {

    private val injector: Injector by inject()
    private val contentRepository = injector.contentRepository
    private val takeRepository = injector.takeRepository

    // The selected project
    private var activeProject: Collection by property()
    val activeProjectProperty = getProperty(ContentGridViewModel::activeProject)

    // Selected child
    private var activeCollection: Collection by property()
    val activeCollectionProperty = getProperty(ContentGridViewModel::activeCollection)

    // Selected resource (e.g. scripture, tN)
    private var activeResource: ResourceMetadata by property()
    val activeResourceProperty = getProperty(ContentGridViewModel::activeResource)

    private var activeContent: Content by property()
    val activeContentProperty = getProperty(ContentGridViewModel::activeContent)

    // List of content to display on the screen
    private val allContent: ObservableList<ContentInfo> = observableArrayList()
    val filteredContents: ObservableList<ContentInfo> = observableArrayList()

    private var loading: Boolean by property(false)
    val loadingProperty = getProperty(ContentGridViewModel::loading)

    val chapterModeEnabledProperty = SimpleBooleanProperty(false)

    private val accessTakes = AccessTakes(contentRepository, takeRepository)

    private fun getFilterContentPredicate(): Predicate<ContentInfo> {
        val label = if (chapterModeEnabledProperty.value) "chapter" else "verse"
        return Predicate<ContentInfo> { it.content.value?.labelKey == label }
    }

    init {
        activeCollectionProperty.toObservable().subscribe { setCollection(it, activeResource) }
        val refilterTriggers = Observable.merge(
                chapterModeEnabledProperty.toObservable(),
                activeResourceProperty.toObservable(),
                allContent.changes()
        )
        refilterTriggers
                .map { Unit }
                .debounce(10, TimeUnit.MILLISECONDS)
                .observeOnFx()
                .subscribe {
                    filteredContents.setAll(
                            allContent
                                    .filtered(getFilterContentPredicate())
                                    .sortedBy { it.content.value?.start }
                    )
                }
    }

    private fun setCollection(collection: Collection, resource: ResourceMetadata) {
        loading = true
        activeCollection = collection
        activeResource =  resource
        allContent.clear()
        fetchContentInfo(collection)
                .doOnComplete {
                    loading = false
                }
                .subscribe {
                    allContent.add(it)
                }
    }

    private fun fetchContentInfo(collection: Collection): Observable<ContentInfo> {
        return contentRepository
                .getByCollection(collection)
                .flatMapObservable {
                    Observable.fromIterable(it)
                }
                .sorted(Comparator.comparing(Content::start))
                .flatMapSingle { content ->
                    accessTakes
                            .getTakeCount(content)
                            .map { ContentInfo(content.toProperty(), SimpleBooleanProperty(it > 0)) }
                }
                .observeOnFx()
    }

    fun viewContentTakes(content: Content) {
        // Launch the select takes page
        // Might be better to use a custom scope to pass the data to the view takes page
        activeContent = content
    }
}