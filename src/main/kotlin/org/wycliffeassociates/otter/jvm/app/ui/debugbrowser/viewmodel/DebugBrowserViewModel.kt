package org.wycliffeassociates.otter.jvm.app.ui.debugbrowser.viewmodel

import com.github.thomasnield.rxkotlinfx.observeOnFx
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Content
import org.wycliffeassociates.otter.common.domain.languages.ImportLanguages
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import tornadofx.ViewModel
import java.util.*

class DebugBrowserViewModel : ViewModel() {
    private val injector: Injector by inject()
    private val collectionRepo = injector.collectionRepo
    private val contentRepo = injector.contentRepository

    val collectionsAndContents: ObservableList<Pair<Collection?, Content?>> =
            FXCollections.observableArrayList<Pair<Collection?, Content?>>()
    val selectedCollectionProperty = SimpleObjectProperty<Collection>()
    val breadcrumb = ArrayDeque<Collection>()

    init {
        ImportLanguages(ClassLoader.getSystemResourceAsStream("content/langnames.json"), injector.languageRepo)
                .import()
                .onErrorComplete()
                .blockingAwait()

        loadProjects()
    }

    fun loadProjects() {
        selectedCollectionProperty.value = null
        breadcrumb.clear()
        loadCollection(null)
    }

    private fun loadCollection(c: Collection?) {
        c?.let { contentRepo.getByCollection(c) }
                ?.observeOnFx()
                ?.doOnSuccess { if (it.isNotEmpty()) collectionsAndContents.setAll(it.map{ Pair(null, it) }) }
                ?.subscribe()
                ?: collectionsAndContents.clear()

        (c?.let(collectionRepo::getChildren) ?: collectionRepo.getRootSources())
                .observeOnFx()
                .doOnSuccess { if (it.isNotEmpty()) collectionsAndContents.setAll(it.map{ Pair(it, null) }) }
                .subscribe()
    }

    fun goBack() {
        val parent = if (breadcrumb.isNotEmpty()) breadcrumb.pop() else null
        selectedCollectionProperty.value = parent
        loadCollection(parent)
    }

    fun openCollection(c: Collection) {
        selectedCollectionProperty.value
                ?.let(breadcrumb::push)
        selectedCollectionProperty.value = c
        loadCollection(c)
    }
}
