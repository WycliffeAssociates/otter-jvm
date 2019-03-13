package org.wycliffeassociates.otter.jvm.app.ui.collectionsgrid.viewmodel

import com.github.thomasnield.rxkotlinfx.observeOnFx
import com.github.thomasnield.rxkotlinfx.toObservable
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.ResourceMetadata
import org.wycliffeassociates.otter.jvm.app.ui.collectionsgrid.view.CollectionTabPane
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import org.wycliffeassociates.otter.jvm.app.ui.mainscreen.view.MainScreenStyles
import tornadofx.CssRule
import tornadofx.ViewModel
import tornadofx.getProperty
import tornadofx.property

class CollectionsGridViewModel : ViewModel() {
    private val injector: Injector by inject()
    private val collectionRepository = injector.collectionRepo
    private val resourceMetadataRepository = injector.resourceMetadataRepository

    // The selected project
    private var activeProject: Collection by property()
    val activeProjectProperty = getProperty(CollectionsGridViewModel::activeProject)

    // List of collection children (i.e. the chapters) to display in the list
    var children: ObservableList<Collection> = FXCollections.observableList(mutableListOf())

    // Selected child
    private var activeCollection: Collection by property()
    val activeCollectionProperty = getProperty(CollectionsGridViewModel::activeCollection)

    private var activeResource: ResourceMetadata by property()
    val activeResourceProperty = getProperty(CollectionsGridViewModel::activeResource)

    private var selectedResourceClass: CssRule by property(MainScreenStyles.scripture)
    val selectedResourceClassProperty = getProperty(CollectionsGridViewModel::selectedResourceClass)

    var resourceList: ObservableList<ResourceMetadata> = FXCollections.observableList(mutableListOf())

    private var loading: Boolean by property(true)
    val loadingProperty = getProperty(CollectionsGridViewModel::loading)

    init {
        activeProjectProperty.toObservable().subscribe {
            updateResources()
            bindChapters()
        }
        activeResourceProperty.toObservable().subscribe {
            it?.let { setSelectedResourceClassProperty(it) }
        }
    }

    private fun updateResources() {
        activeCollectionProperty.value = null
        resourceList.clear()
        activeProject.resourceContainer
                ?.let { resourceMetadataRepository.getLinkedToSource(it) }
                ?.observeOnFx()
                ?.subscribe { linked ->
                    val sourceAndLinked = listOfNotNull(activeProject.resourceContainer) + linked
                    resourceList.setAll(sourceAndLinked)
                }
    }

    private fun bindChapters() {
        activeCollectionProperty.value = null
        children.clear()
        collectionRepository
                .getChildren(activeProject)
                .observeOnFx()
                .subscribe { childCollections ->
                    // Now we have the children of the project collection
                    loading = false
                    children.addAll(childCollections.sortedBy { it.sort })
                }
    }

    fun selectCollection(child: Collection) {
        activeCollection = child
    }

    private fun setSelectedResourceClassProperty(resource: ResourceMetadata) {
        val cssRule = when {
            resource.identifier == CollectionTabPane.ResourceIdentifier.TN.text ->
                MainScreenStyles.translationNotes
            else ->
                MainScreenStyles.scripture
        }

        selectedResourceClassProperty.set(cssRule)
    }
}