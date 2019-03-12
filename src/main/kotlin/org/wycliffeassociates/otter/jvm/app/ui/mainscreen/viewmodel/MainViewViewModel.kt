package org.wycliffeassociates.otter.jvm.app.ui.mainscreen.viewmodel

import com.github.thomasnield.rxkotlinfx.observeOnFx
import javafx.beans.property.*
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Content
import org.wycliffeassociates.otter.common.data.model.Take
import org.wycliffeassociates.otter.jvm.app.ui.mainscreen.view.MainScreenView
import org.wycliffeassociates.otter.jvm.app.ui.collectionsgrid.view.CollectionsGrid
import org.wycliffeassociates.otter.jvm.app.ui.contentgrid.view.ContentGrid
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import org.wycliffeassociates.otter.jvm.app.ui.takemanagement.view.TakeManagementView
import tornadofx.*

class MainViewViewModel : ViewModel() {
    private val injector: Injector by inject()
    private val contentRepository = injector.contentRepository

    val selectedProjectProperty = SimpleObjectProperty<Collection>()
    val selectedProjectName = SimpleStringProperty()
    val selectedProjectLanguage = SimpleStringProperty()

    val selectedCollectionProperty = SimpleObjectProperty<Collection>()
    val selectedCollectionTitle = SimpleStringProperty()
    val selectedCollectionBody = SimpleStringProperty()

    val selectedContentProperty = SimpleObjectProperty<Content>()
    val selectedContentTitle = SimpleStringProperty()
    val selectedContentBody = SimpleStringProperty()
    val selectedTake = SimpleObjectProperty<Take>()
    val isChapterMode = SimpleBooleanProperty()

    private val takesPageDocked = SimpleBooleanProperty(false)

    init {
        selectedProjectProperty.onChange {
            if (it != null) {
                projectSelected(it)
            }
        }

        selectedCollectionProperty.onChange {
            if (it != null) {
                collectionSelected(it)
            }
        }

        selectedContentProperty.onChange {
            if (it != null) {
                contentSelected(it)
            }
            else { // the take manager was undocked
                takesPageDocked.set(false)
            }
        }
    }

    fun projectSelected(selectedProject: Collection) {
        setActiveProjectText(selectedProject)

        find<MainScreenView>().activeFragment.dock<CollectionsGrid>()
        CollectionsGrid().apply {
            activeProject.bindBidirectional(selectedProjectProperty)
            activeCollection.bindBidirectional(selectedCollectionProperty)
        }
    }

    fun collectionSelected(collection: Collection) {
        setActiveCollectionText(collection)

        find<MainScreenView>().activeFragment.dock<ContentGrid>()
        ContentGrid().apply {
            activeProject.bindBidirectional(selectedProjectProperty)
            activeCollection.bindBidirectional(selectedCollectionProperty)
            activeContent.bindBidirectional(selectedContentProperty)
            isChapterMode.bindBidirectional(chapterMode)
        }
    }

    fun contentSelected(content: Content) {
        setActiveContentText(content)

        if(takesPageDocked.value == false) {
            find<MainScreenView>().activeFragment.dock<TakeManagementView>()
            TakeManagementView().apply {
                activeProject.bindBidirectional(selectedProjectProperty)
                activeCollection.bindBidirectional(selectedCollectionProperty)
                activeContent.bindBidirectional(selectedContentProperty)
                activeSelectedTake.bindBidirectional(selectedTake)
            }
        }
        takesPageDocked.set(true)
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
}