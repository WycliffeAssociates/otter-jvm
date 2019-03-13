package org.wycliffeassociates.otter.jvm.app.ui.contentgrid.viewmodel

import com.github.thomasnield.rxkotlinfx.changes
import com.github.thomasnield.rxkotlinfx.observeOnFx
import com.github.thomasnield.rxkotlinfx.toObservable
import io.reactivex.Observable
import javafx.beans.property.*
import javafx.collections.FXCollections
import javafx.collections.FXCollections.*
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Content
import org.wycliffeassociates.otter.common.data.model.ResourceMetadata
import org.wycliffeassociates.otter.common.domain.content.AccessTakes
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import tornadofx.*
import java.util.concurrent.TimeUnit

// Boolean tracks whether the content has takes associated with it
typealias ContentTuple = Pair<SimpleObjectProperty<Content>, SimpleBooleanProperty>
typealias GroupedContent = Pair<SimpleIntegerProperty, ListProperty<ContentTuple>>

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
    private val allContent: ObservableList<ContentTuple> = observableArrayList()
    val filteredContent: ObservableList<GroupedContent> = observableArrayList()

    private var loading: Boolean by property(false)
    val loadingProperty = getProperty(ContentGridViewModel::loading)

    val chapterModeEnabledProperty = SimpleBooleanProperty(false)
    private val accessTakes = AccessTakes(contentRepository, takeRepository)

    private fun getFilterContentPredicate(): (ContentTuple) -> Boolean = when {
        chapterModeEnabledProperty.value -> {
            c -> c.first.value?.labelKey == "chapter"
        }
        activeResourceProperty.value?.type == "help" -> {
            c -> c.first.value?.labelKey == "title" || c.first.value?.labelKey == "body"
        }
        else -> {
            c -> c.first.value?.labelKey == "verse"
        }
    }

    init {
        activeCollectionProperty.toObservable().subscribe { selectChildCollection(it, activeResource) }
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
                    filteredContent.setAll(
                            allContent
                                    .filtered(getFilterContentPredicate())
                                    .groupByTo(sortedMapOf<Int, MutableList<ContentTuple>>()) {
                                        it.first.value.start
                                    }
                                    .map { mapEntry ->
                                        val start = SimpleIntegerProperty(mapEntry.key)
                                        val contents = SimpleListProperty(observableList(mapEntry.value))
                                        GroupedContent(start, contents)
                                    }
                    )
                }
    }

    private fun selectChildCollection(child: Collection, resource: ResourceMetadata) {
        activeCollection = child
        activeResource =  resource
        // Remove existing content so the user knows they are outdated
        allContent.clear()
        loading = true
        contentRepository
                .getByCollection(child)
                .flatMapObservable {
                    Observable.fromIterable(it)
                }
                .flatMapSingle { content ->
                    accessTakes
                            .getTakeCount(content)
                            .map { ContentTuple(content.toProperty(), SimpleBooleanProperty(it > 0)) }
                }
                .toList()
                .observeOnFx()
                .subscribe { retrieved ->
                    allContent.clear() // Make sure any content that might have been added are removed
                    allContent.addAll(retrieved)
                    loading = false
                }
    }

    fun viewContentTakes(content: Content) {
        // Launch the select takes page
        // Might be better to use a custom scope to pass the data to the view takes page
        activeContent = content
    }
}