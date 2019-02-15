package org.wycliffeassociates.otter.jvm.app.ui.collectionsgrid.viewmodel

import com.github.thomasnield.rxkotlinfx.observeOnFx
import com.github.thomasnield.rxkotlinfx.toObservable
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Content
import org.wycliffeassociates.otter.common.domain.content.AccessTakes
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import tornadofx.*

class CollectionsGridViewModel: ViewModel() {
    private val injector: Injector by inject()
    private val directoryProvider = injector.directoryProvider
    private val collectionRepository = injector.collectionRepo
    private val contentRepository = injector.contentRepository
    private val takeRepository = injector.takeRepository
    private val pluginRepository = injector.pluginRepository

    // The selected project
    private var activeProject: Collection by property()
    val activeProjectProperty = getProperty(CollectionsGridViewModel::activeProject)

    val projectTitle: SimpleStringProperty = SimpleStringProperty("")

    // List of collection children (i.e. the chapters) to display in the list
    var children: ObservableList<Collection> = FXCollections.observableList(mutableListOf())

    // Selected child
    private var activeCollection: Collection by property()
    val activeCollectionProperty = getProperty(CollectionsGridViewModel::activeCollection)

    // List of content to display on the screen
    // Boolean tracks whether the content has takes associated with it
    val allContent: ObservableList<Pair<SimpleObjectProperty<Content>, SimpleBooleanProperty>>
            = FXCollections.observableArrayList()
    val filteredContent: ObservableList<Pair<SimpleObjectProperty<Content>, SimpleBooleanProperty>>
            = FXCollections.observableArrayList()

    // Whether the UI should show the plugin as active
    private var showPluginActive: Boolean by property(false)
    val showPluginActiveProperty = getProperty(CollectionsGridViewModel::showPluginActive)


    private var loading: Boolean by property(false)
    val loadingProperty = getProperty(CollectionsGridViewModel::loading)

    // Create the use cases we need (the model layer)
    private val accessTakes = AccessTakes(contentRepository, takeRepository)

    init {
        activeProjectProperty.toObservable().subscribe { setTitleAndChapters() }
    }

    private fun setTitleAndChapters() {
        val project = activeProjectProperty.value
        activeCollectionProperty.value = null
        if (project != null) {
            projectTitle.set(project.titleKey)
            children.clear()
            filteredContent.clear()
            collectionRepository
                    .getChildren(project)
                    .observeOnFx()
                    .subscribe { childCollections ->
                        // Now we have the children of the project collection
                        children.addAll(childCollections.sortedBy { it.sort })
                    }
        }
    }

    fun selectCollection(child: Collection) {
        activeCollection = child
        allContent.clear()
    }

}