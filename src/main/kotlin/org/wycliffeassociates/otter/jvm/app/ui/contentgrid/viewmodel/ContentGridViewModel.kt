package org.wycliffeassociates.otter.jvm.app.ui.contentgrid.viewmodel

import com.github.thomasnield.rxkotlinfx.changes
import com.github.thomasnield.rxkotlinfx.observeOnFx
import com.github.thomasnield.rxkotlinfx.toObservable
import io.reactivex.Observable
import javafx.beans.property.*
import javafx.collections.FXCollections.observableArrayList
import javafx.collections.FXCollections.observableList
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Content
import org.wycliffeassociates.otter.common.data.model.ResourceMetadata
import org.wycliffeassociates.otter.common.domain.content.AccessTakes
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import tornadofx.*
import java.util.concurrent.TimeUnit

data class ContentInfo(
        val content: SimpleObjectProperty<Content>,
        val hasTake: SimpleBooleanProperty)

data class GroupedContents(
        val label: SimpleIntegerProperty,
        val content: ListProperty<ContentInfo>)

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
    val filteredContents: ObservableList<GroupedContents> = observableArrayList()

    private var loading: Boolean by property(false)
    val loadingProperty = getProperty(ContentGridViewModel::loading)

    val chapterModeEnabledProperty = SimpleBooleanProperty(false)

    private val accessTakes = AccessTakes(contentRepository, takeRepository)

    private fun getFilterContentPredicate(): (ContentInfo) -> Boolean = when {
        chapterModeEnabledProperty.value -> {
            c -> c.content.value?.labelKey == "chapter"
        }
        activeResourceProperty.value?.type == "help" -> {
            c -> c.content.value?.labelKey == "title" || c.content.value?.labelKey == "body"
        }
        else -> {
            c -> c.content.value?.labelKey == "verse"
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
                    filteredContents.setAll(
                            allContent
                                    .filtered(getFilterContentPredicate())
                                    .groupByTo(sortedMapOf<Int, MutableList<ContentInfo>>()) {
                                        it.content.value.start
                                    }
                                    .map { mapEntry ->
                                        val start = SimpleIntegerProperty(mapEntry.key)
                                        val contents = SimpleListProperty(observableList(mapEntry.value))
                                        GroupedContents(start, contents)
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
                            .map { ContentInfo(content.toProperty(), SimpleBooleanProperty(it > 0)) }
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