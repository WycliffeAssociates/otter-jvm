package org.wycliffeassociates.otter.jvm.app.ui.mainscreen

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import javafx.scene.Node
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Content
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import org.wycliffeassociates.otter.jvm.app.ui.projecteditor.view.ProjectEditor
import org.wycliffeassociates.otter.jvm.app.ui.projecthome.view.ProjectHomeView
import org.wycliffeassociates.otter.jvm.app.ui.viewtakes.view.ViewTakesView
import tornadofx.*
import java.util.*

class MainViewViewModel: ViewModel() {
    private val injector: Injector by inject()

    val selectedProjectProperty = SimpleObjectProperty<Collection>()
    val selectedProjectName = SimpleStringProperty()
    val selectedProjectLanguage = SimpleStringProperty()

    val selectedCollectionProperty = SimpleObjectProperty<Collection>()
    val selectedCollectionTitle = SimpleStringProperty()
    val selectedCollectionBody = SimpleStringProperty()

    val selectedContentProperty = SimpleObjectProperty<Content>()
    val selectedContentTitle = SimpleStringProperty()
    val selectedContentBody = SimpleStringProperty()

    var activeFragment: Fragment = ProjectHomeView()


    var fragmentStack=  Stack<Node>()

    init {
        selectedProjectProperty.onChange {
            if(it!= null) {
                projectSelected(it)
                setActiveProjectText(it)
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

        find<MainScreenView>().activeFragment.dock<ProjectEditor>()
        ProjectEditor().apply {
            selectedCollectionProperty.bind(activeCollection)
            selectedContentProperty.bind(activeContent)
        }
    }

    fun collectionSelected(collection: Collection) {
        setActiveCollectionText(collection)
    }

    fun contentSelected(content: Content) {
        setActiveContentText(content)
        find<MainScreenView>().activeFragment.dock<ViewTakesView>()
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

    fun goBack() {
//        find<MainScreenView>().activeFragment.children.clear()
        val node = fragmentStack.peek()
        fragmentStack.map {

        }
    }
}