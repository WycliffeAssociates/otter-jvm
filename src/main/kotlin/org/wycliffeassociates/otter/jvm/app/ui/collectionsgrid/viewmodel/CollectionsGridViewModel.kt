package org.wycliffeassociates.otter.jvm.app.ui.collectionsgrid.viewmodel

import com.github.thomasnield.rxkotlinfx.observeOnFx
import com.github.thomasnield.rxkotlinfx.toObservable
import javafx.beans.property.*
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Content
import org.wycliffeassociates.otter.common.domain.content.AccessTakes
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import tornadofx.*

class CollectionsGridViewModel : ViewModel() {
    private val injector: Injector by inject()
    private val directoryProvider = injector.directoryProvider
    private val collectionRepository = injector.collectionRepo
    private val contentRepository = injector.contentRepository
    private val takeRepository = injector.takeRepository
    private val pluginRepository = injector.pluginRepository

    // The selected project
    private var activeProject: Collection by property()
    val activeProjectProperty = getProperty(CollectionsGridViewModel::activeProject)

    // List of collection children (i.e. the chapters) to display in the list
    var children: ObservableList<Collection> = FXCollections.observableList(mutableListOf())

    // Selected child
    private var activeCollection: Collection by property()
    val activeCollectionProperty = getProperty(CollectionsGridViewModel::activeCollection)

    // List of content to display on the screen
    // Boolean tracks whether the content has takes associated with it
    val allContent: ObservableList<Pair<SimpleObjectProperty<Collection>, DoubleProperty>> = FXCollections.observableArrayList()
    val filteredContent: ObservableList<Pair<SimpleObjectProperty<Collection>, DoubleProperty>> = FXCollections.observableArrayList()

    private var loading: Boolean by property(true)
    val loadingProperty = getProperty(CollectionsGridViewModel::loading)

    // Create the use cases we need (the model layer)
    private val accessTakes = AccessTakes(contentRepository, takeRepository)

    init {
        activeProjectProperty.toObservable().subscribe {
            bindChapters()
        }
    }

    fun getProgress(collection: Collection): DoubleProperty {
        val percentage = SimpleDoubleProperty(0.0)
        contentRepository
                .getByCollection(collection)
                .observeOnFx()
                .subscribe { contentList ->
                    percentage.set(computeProgress(contentList))
                }
        return percentage
    }

    private fun computeProgress(contentList: List<Content>): Double {
        var total = contentList.size.toDouble() - 1.0 //minus 1 because the chapter take/content is at pos 0
        var completed = 0.0

        for (content in contentList) {
            if (content.selectedTake != null) {
                completed++
            }
        }
        return completed / total
    }

    private fun bindChapters() {
        loading = true
        activeCollectionProperty.value = null
        children.clear()
        allContent.clear()
        filteredContent.clear()
        if (activeProject != null) {
            collectionRepository
                    .getChildren(activeProject)
                    .observeOnFx()
                    .subscribe { childCollections ->
                        // Now we have the children of the project collection
                        loading = false
                        children.addAll(childCollections.sortedBy { it.sort })
                        children.forEach {
                            var progress = getProgress(it)
                            allContent.add(Pair(it.toProperty(),progress))
                        }
                    }
        }
    }

    fun selectCollection(child: Collection) {
        activeCollection = child
        allContent.clear()
    }

    fun refresh() {
        if (activeProject != null) {
            bindChapters()
        }
    }
}