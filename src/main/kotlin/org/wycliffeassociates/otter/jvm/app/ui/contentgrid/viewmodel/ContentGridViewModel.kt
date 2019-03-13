package org.wycliffeassociates.otter.jvm.app.ui.contentgrid.viewmodel

import com.github.thomasnield.rxkotlinfx.changes
import com.github.thomasnield.rxkotlinfx.observeOnFx
import com.github.thomasnield.rxkotlinfx.toObservable
import io.reactivex.Observable
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Content
import org.wycliffeassociates.otter.common.domain.content.AccessTakes
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import tornadofx.*

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

    private var activeContent: Content by property()
    val activeContentProperty = getProperty(ContentGridViewModel::activeContent)

    // List of content to display on the screen
    // Boolean tracks whether the content has takes associated with it
    data class ContentGridItem(val content: Content, val hasTakes: BooleanProperty)
    val allGridItems: ObservableList<ContentGridItem> = FXCollections.observableArrayList()
    val filteredGridItems: ObservableList<ContentGridItem> = FXCollections.observableArrayList()

    // Whether the UI should show the plugin as active

    private var loading: Boolean by property(false)
    val loadingProperty = getProperty(ContentGridViewModel::loading)

    val chapterModeEnabledProperty = SimpleBooleanProperty(false)
    private val accessTakes = AccessTakes(contentRepository, takeRepository)

    init {
        activeCollectionProperty.toObservable().subscribe { selectChildCollection(it) }
        Observable.merge(chapterModeEnabledProperty.toObservable(), allGridItems.changes()).subscribe { _ ->
            filteredGridItems.setAll(
                    if (chapterModeEnabledProperty.value == true) {
                        allGridItems.filtered { it.content.labelKey == "chapter" }
                    } else {
                        allGridItems.filtered { it.content.labelKey != "chapter" }
                    }
            )
        }
    }

    fun selectChildCollection(child: Collection) {
        activeCollection = child
        // Remove existing content so the user knows they are outdated
        allGridItems.clear()
        loading = true
        contentRepository
                .getByCollection(child)
                .flatMapObservable {
                    Observable.fromIterable(it)
                }
                .flatMapSingle { content ->
                    accessTakes
                            .getTakeCount(content)
                            .map { ContentGridItem(content, SimpleBooleanProperty(it > 0)) }
                }
                .toList()
                .observeOnFx()
                .subscribe { retrieved ->
                    retrieved.sortBy { it.content.sort }
                    allGridItems.clear() // Make sure any content that might have been added are removed
                    allGridItems.addAll(retrieved)
                    loading = false
                }
    }

    fun viewContentTakes(content: Content) {
        // Launch the select takes page
        // Might be better to use a custom scope to pass the data to the view takes page
        activeContent = content
    }

    fun refresh() {
        if(activeCollection != null) {
        selectChildCollection(activeCollection)
        }
    }
}