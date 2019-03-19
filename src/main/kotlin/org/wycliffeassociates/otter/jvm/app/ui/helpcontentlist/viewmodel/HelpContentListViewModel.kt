package org.wycliffeassociates.otter.jvm.app.ui.helpcontentlist.viewmodel

import com.github.thomasnield.rxkotlinfx.changes
import com.github.thomasnield.rxkotlinfx.observeOnFx
import com.github.thomasnield.rxkotlinfx.toObservable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
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
        val derived: ObjectProperty<Content>,
        val source: ObjectProperty<Content?>,
        val hasTake: BooleanProperty)

data class GroupedContents(
        val label: IntegerProperty,
        val contentInfos: ListProperty<ContentInfo>)

class HelpContentListViewModel: ViewModel() {

    private val injector: Injector by inject()
    private val contentRepository = injector.contentRepository
    private val takeRepository = injector.takeRepository

    // The selected project
    private var activeProject: Collection by property()
    val activeProjectProperty = getProperty(HelpContentListViewModel::activeProject)

    // Selected child
    private var activeCollection: Collection by property()
    val activeCollectionProperty = getProperty(HelpContentListViewModel::activeCollection)

    // Selected resource (e.g. scripture, tN)
    private var activeResource: ResourceMetadata by property()
    val activeResourceProperty = getProperty(HelpContentListViewModel::activeResource)

    private var activeContent: Content by property()
    val activeContentProperty = getProperty(HelpContentListViewModel::activeContent)

    // List of content to display on the screen
    private val allContent: ObservableList<ContentInfo> = observableArrayList()
    val filteredContents: ObservableList<GroupedContents> = observableArrayList()

    private var loading: Boolean by property(false)
    val loadingProperty = getProperty(HelpContentListViewModel::loading)

    val chapterModeEnabledProperty = SimpleBooleanProperty(false)

    private val accessTakes = AccessTakes(contentRepository, takeRepository)

    init {
        activeCollectionProperty.toObservable().subscribe {
            setCollection(it, activeResource)
        }

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
                    filteredContents.setAll(allContent
                                    .filtered(this::isContentHelp)
                                    .groupByTo(sortedMapOf<Int, MutableList<ContentInfo>>()) {
                                        it.derived.value.start
                                    }
                                    .map { (start: Int, contents: MutableList<ContentInfo>) ->
                                        val sorted = contents.sortedBy { it.derived.value.sort }
                                        GroupedContents(
                                                start.toProperty(),
                                                SimpleListProperty(observableList(sorted)))
                                    })
                }
    }

    private fun setCollection(collection: Collection, resource: ResourceMetadata) {
        activeCollection = collection
        activeResource =  resource
        // Remove existing content so the user knows they are outdated
        allContent.clear()
        loading = true
        contentRepository
                .getByCollection(collection)
                .flatMapObservable { contentList ->
                    Observable.fromIterable(contentList)
                }
                .flatMapMaybe { derivedContent ->
                    val hasTakes = accessTakes
                            .getTakeCount(derivedContent)
                            .map { it > 0 }
                            .toMaybe()
                    val sourceContent = contentRepository
                            .getSources(derivedContent)
                            .flatMapObservable {
                                Observable.fromIterable(it
                                        .filter(this::isContentHelp)
                                        .sortedByDescending { it.start })
                            }
                            .firstElement()
                    Maybe.zip<Boolean, Content, ContentInfo>(hasTakes, sourceContent, BiFunction { ht, sc ->
                        ContentInfo(
                                SimpleObjectProperty(derivedContent),
                                SimpleObjectProperty(sc),
                                SimpleBooleanProperty(ht))
                    })
                }
                .toList()
                .observeOnFx()
                .subscribe { retrieved ->
                    allContent.setAll(retrieved)
                    loading = false
                }
    }

    fun viewContentTakes(content: Content) {
        // Launch the select takes page
        // Might be better to use a custom scope to pass the data to the view takes page
        activeContent = content
    }

    private fun isContentHelp(contentInfo: ContentInfo): Boolean {
        return isContentHelp(contentInfo.derived.value)
    }

    private fun isContentHelp(content: Content): Boolean {
        return content.labelKey.let { it == "title" || it == "body" }
    }
}