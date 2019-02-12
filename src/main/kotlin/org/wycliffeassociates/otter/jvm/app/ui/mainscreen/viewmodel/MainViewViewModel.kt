package org.wycliffeassociates.otter.jvm.app.ui.mainscreen.viewmodel

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Content
import org.wycliffeassociates.otter.jvm.app.ui.mainscreen.view.MainScreenView
import org.wycliffeassociates.otter.jvm.app.ui.viewcollections.view.ViewCollections
import org.wycliffeassociates.otter.jvm.app.ui.viewcontent.view.ViewContent
import org.wycliffeassociates.otter.jvm.app.ui.viewtakes.view.ViewTakesView
import tornadofx.*

class MainViewViewModel: ViewModel() {
    val selectedProjectProperty = SimpleObjectProperty<Collection>()
    val selectedProjectName = SimpleStringProperty()
    val selectedProjectLanguage = SimpleStringProperty()

    val selectedCollectionProperty = SimpleObjectProperty<Collection>()
    val selectedCollectionTitle = SimpleStringProperty()
    val selectedCollectionBody = SimpleStringProperty()

    val selectedContentProperty = SimpleObjectProperty<Content>()
    val selectedContentTitle = SimpleStringProperty()
    val selectedContentBody = SimpleStringProperty()

    init {
        selectedProjectProperty.onChange {
            if(it!= null) {
                projectSelected(it)
            }
        }

        selectedCollectionProperty.onChange {
            if(it != null) {
                collectionSelected(it)
            }
        }

        selectedContentProperty.onChange {
            if(it != null) {
                contentSelected(it)
            }
        }
    }

    fun projectSelected(selectedProject: Collection) {
        setActiveProjectText(selectedProject)

        find<MainScreenView>().activeFragment.dock<ViewCollections>()
        ViewCollections().apply {
            activeProject.bindBidirectional(selectedProjectProperty)
            activeCollection.bindBidirectional(selectedCollectionProperty)
        }
    }

    fun collectionSelected(collection: Collection) {
        setActiveCollectionText(collection)

        find<MainScreenView>().activeFragment.dock<ViewContent>()
        ViewContent().apply {
            activeProject.bindBidirectional(selectedProjectProperty)
            activeCollection.bindBidirectional(selectedCollectionProperty)
            activeContent.bindBidirectional(selectedContentProperty)

        }
    }

    fun contentSelected(content: Content) {
        setActiveContentText(content)
        find<MainScreenView>().activeFragment.dock<ViewTakesView>()
        ViewTakesView().apply {
            activeProject.bindBidirectional(selectedProjectProperty)
            activeCollection.bindBidirectional(selectedCollectionProperty)
            activeContent.bindBidirectional(selectedContentProperty)
        }
    }

    fun setActiveContentText(content: Content) {
        selectedContentTitle.set(content.labelKey.toUpperCase())
        selectedContentBody.set(content.start.toString())
    }

    fun setActiveCollectionText(collection: Collection) {
        selectedCollectionTitle.set(collection.labelKey.toUpperCase())
        selectedCollectionBody.set(collection.titleKey)
    }

    fun setActiveProjectText(activeProject: Collection) {
        selectedProjectName.set(activeProject.titleKey)
        selectedProjectLanguage.set(activeProject.resourceContainer?.language?.name)
    }
}