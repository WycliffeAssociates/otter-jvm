package org.wycliffeassociates.otter.jvm.app.ui.projectcreation.view.fragments

<<<<<<< HEAD
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.layout.HBox
import org.wycliffeassociates.otter.jvm.app.ui.chapterPage.model.Project
import org.wycliffeassociates.otter.jvm.app.ui.projectcreation.viewmodel.ProjectCreationViewModel
import tornadofx.*


=======
import javafx.geometry.Pos
import org.wycliffeassociates.otter.jvm.app.ui.projectcreation.viewmodel.ProjectCreationViewModel
import tornadofx.*

>>>>>>> building, opening ui
class SelectBook() : View() {
    val viewModel: ProjectCreationViewModel by inject()
    //val root = DataGrid<Project>()
//    var mappedList: ObservableList<Project>
    init {
//        mappedList = viewmodel.projectsProperty.value.map {
//            add
//        }
    }

    override val root =
            datagrid(listOf("string")) {
                cellCache {
                    vbox(10) {
                        alignment = Pos.CENTER
                        //label(it.book.title)
                        button()
                    }
            }
        }
<<<<<<< HEAD
    }


=======
    }
>>>>>>> building, opening ui
