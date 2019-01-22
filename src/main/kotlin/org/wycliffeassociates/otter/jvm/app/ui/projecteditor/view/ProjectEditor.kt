package org.wycliffeassociates.otter.jvm.app.ui.projecteditor.view

import com.github.thomasnield.rxkotlinfx.toObservable
import com.jfoenix.controls.JFXSnackbar
import com.jfoenix.controls.JFXToggleButton
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.layout.FlowPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import org.wycliffeassociates.otter.jvm.app.theme.AppStyles
import org.wycliffeassociates.otter.jvm.app.ui.projecteditor.ChapterContext
import org.wycliffeassociates.otter.jvm.app.ui.projecteditor.viewmodel.ProjectEditorViewModel
import org.wycliffeassociates.otter.jvm.app.widgets.chaptercard.chaptercard
import org.wycliffeassociates.otter.jvm.app.widgets.progressdialog.progressdialog
import org.wycliffeassociates.otter.jvm.app.widgets.projectnav.projectnav
import org.wycliffeassociates.otter.jvm.app.widgets.versecard.versecard
import tornadofx.*

class ProjectEditor : View() {
    private val viewModel: ProjectEditorViewModel by inject()
    private var childrenList by singleAssign<VBox>()
    private var contextMenu by singleAssign<ListMenu>()
    var chapterFlowPane by singleAssign<FlowPane>()
    var verseFlowPane by singleAssign<FlowPane>()
    var chapterScrollPane by singleAssign<ScrollPane>()
    var verseScrollPane by singleAssign<ScrollPane>()

    init {
        importStylesheet<ProjectEditorStyles>()
    }

    override fun onDock() {
        super.onDock()
        // Make sure we refresh the content if need be
        // The content selected take could have changed since last docked
        viewModel.filteredContent.forEach {
            // null the content and then reassign it to force
            // property on change to be called and update the bound card
            val tmp = it.first.value
            it.first.value = null
            it.first.value = tmp
        }
        viewModel.refreshActiveContent()


        if (viewModel.activeChildProperty.value == null) {
            // No chapter has been selected
            // Reset to chapter selection
            showAvailableChapters()
        }
    }

    override val root = stackpane {
        val snackBar = JFXSnackbar(this)
        viewModel.snackBarObservable.subscribe { message ->
            snackBar.enqueue(
                    JFXSnackbar.SnackbarEvent(message, messages["addPlugin"].toUpperCase(), 5000, false, EventHandler {
                        viewModel.addPlugin(message == messages["noRecorder"], message == messages["noEditor"])
                    })
            )
        }
        addClass(AppStyles.appBackground)
        hbox {
            vbox {
                vgrow = Priority.ALWAYS
                childrenList = projectnav {
                    vgrow = Priority.ALWAYS
                    chapterBox.apply {
                        onMouseClicked = EventHandler {
                            showAvailableChapters()
                        }
                    }
                    projectBox.apply {
                        onMouseClicked = EventHandler {
                            navigateBack()
                        }
                    }
                    activeProjectProperty.bind(viewModel.projectProperty)
                    activeChapterProperty.bind(viewModel.activeChildProperty)
                    activeContentProperty.bind(viewModel.activeContentProperty)
                    selectProjectText = messages["selectProject"]
                    selectChapterText= messages["selectChapter"]
                    selectChunkText = messages["selectChunk"]
                }
                add(childrenList)
            }

            vbox {
                hgrow = Priority.ALWAYS
                hbox {
                    addClass(ProjectEditorStyles.backButtonContainer)
                    // Mode toggle
                    add(JFXToggleButton().apply {
                        text = messages["chapterMode"]
                        viewModel.chapterModeEnabledProperty.bind(selectedProperty())
                        addClass(AppStyles.appToggleButton)
                    })
                }
                vbox {
                    vgrow = Priority.ALWAYS
                    progressindicator {
                        visibleProperty().bind(viewModel.loadingProperty)
                        managedProperty().bind(visibleProperty())
                        addClass(ProjectEditorStyles.contentLoadingProgress)
                    }
                    chapterScrollPane = scrollpane {
                        isFitToHeight = true
                        isFitToWidth = true
                        chapterFlowPane = flowpane {
                            addClass(AppStyles.appBackground)
                            addClass(ProjectEditorStyles.collectionsFlowpane)
                            bindChildren(viewModel.children) {
                                vbox {
                                    alignment = Pos.CENTER
                                    add(chaptercard(it) {
                                        addClass(ProjectEditorStyles.collectionCard)
                                        chapterGraphic.apply { addClass(ProjectEditorStyles.chaptercardGraphic) }
                                        cardNumber.addClass(ProjectEditorStyles.cardNumber)
                                        cardButton.addClass(ProjectEditorStyles.cardButton)
                                        cardBackground.addClass(ProjectEditorStyles.cardBackground)
                                        progressbar.addClass(ProjectEditorStyles.cardProgressbar)
                                        progressbar.hide()
                                        cardButton.apply {
                                            action {
                                                viewModel.selectChildCollection(it)
                                                verseScrollPane.show()
                                                chapterScrollPane.hide()
                                            }
                                        }
                                    })
                                }
                            }
                        }
                    }
                    verseScrollPane = scrollpane {
                        isFitToWidth = true
                        isFitToHeight = true
                        verseFlowPane = flowpane {
                            addClass(AppStyles.appBackground)
                            addClass(ProjectEditorStyles.collectionsFlowpane)
                            bindChildren(viewModel.filteredContent) {
                                vbox {
                                    alignment = Pos.CENTER
                                    add(versecard(it.first.value) {
                                        addClass(ProjectEditorStyles.collectionCard)
                                        chapterGraphic.apply { addClass(ProjectEditorStyles.versecardGraphic) }
                                        cardBackground.addClass(ProjectEditorStyles.cardBackground)
                                        cardNumber.addClass(ProjectEditorStyles.cardNumber)
                                        cardButton.addClass(ProjectEditorStyles.cardButton)
                                        cardButton.apply {
                                            action {
                                                viewModel.viewContentTakes(it.first.value)
                                            }
                                        }
                                    })
                                }
                            }
                        }
                    }
                    //initially hide the verseScrollPane
                    verseScrollPane.hide()
                    addClass(ProjectEditorStyles.contentGridContainer)
                }
            }
        }

        val dialog = progressdialog {
            root.addClass(AppStyles.progressDialog)
            viewModel.contextProperty.toObservable().subscribe { newContext ->
                when (newContext) {
                    ChapterContext.RECORD -> graphic = AppStyles.recordIcon("60px")
                    ChapterContext.EDIT_TAKES -> graphic = AppStyles.editIcon("60px")
                    else -> {
                    }
                }
            }
        }
        viewModel.showPluginActiveProperty.onChange { value ->
            Platform.runLater {
                if (value == true) {
                    dialog.open()
                } else {
                    dialog.close()
                }
            }
        }

        viewModel.activeContentProperty.onChange {
            if (it == null && viewModel.activeChildProperty.value != null) {
                //user navigated back to verse selection
                verseScrollPane.show()
                chapterScrollPane.hide()
            }
            else if (it == null && viewModel.activeChildProperty.value == null) {
                //user navigated back to chapter collection
                showAvailableChapters()
            }
        }

    }

    private fun navigateBack() {
        viewModel.refreshNav()
        workspace.navigateBack()
        viewModel.reset()
    }

    private fun showAvailableChapters() {
        verseScrollPane.hide()
        chapterScrollPane.show()
        viewModel.activeChildProperty.value = null
    }
}



