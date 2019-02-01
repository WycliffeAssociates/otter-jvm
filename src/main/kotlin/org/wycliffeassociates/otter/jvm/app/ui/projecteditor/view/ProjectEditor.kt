package org.wycliffeassociates.otter.jvm.app.ui.projecteditor.view

import com.github.thomasnield.rxkotlinfx.toObservable
import com.jfoenix.controls.JFXSnackbar
import com.jfoenix.controls.JFXToggleButton
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.application.Platform
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.event.EventHandler
import javafx.scene.control.ScrollPane
import javafx.scene.layout.FlowPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Screen
import org.wycliffeassociates.otter.common.data.model.Collection
import org.wycliffeassociates.otter.common.data.model.Content
import org.wycliffeassociates.otter.jvm.app.theme.AppStyles
import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
import org.wycliffeassociates.otter.jvm.app.ui.projecteditor.ChapterContext
import org.wycliffeassociates.otter.jvm.app.ui.projecteditor.viewmodel.ProjectEditorViewModel
import org.wycliffeassociates.otter.jvm.app.widgets.card.DefaultStyles
import org.wycliffeassociates.otter.jvm.app.widgets.card.card
import org.wycliffeassociates.otter.jvm.app.widgets.chaptercard.chaptercard
import org.wycliffeassociates.otter.jvm.app.widgets.progressdialog.progressdialog
import org.wycliffeassociates.otter.jvm.app.widgets.versecard.versecard
import tornadofx.*
import java.net.URI

class ProjectEditor : View() {
    private val viewModel: ProjectEditorViewModel by inject()
    private var childrenList by singleAssign<VBox>()
    private var contextMenu by singleAssign<ListMenu>()
    var chapterFlowPane by singleAssign<FlowPane>()
    var verseFlowPane by singleAssign<FlowPane>()
    var chapterScrollPane by singleAssign<ScrollPane>()
    var verseScrollPane by singleAssign<ScrollPane>()

    val activeCollection: ReadOnlyObjectProperty<Collection> = viewModel.activeChildProperty
    val activeContent: ReadOnlyObjectProperty<Content> = viewModel.activeContentProperty

    init {
        importStylesheet<ProjectEditorStyles>()
        importStylesheet<DefaultStyles>()
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
        hgrow = Priority.ALWAYS
        vgrow = Priority.ALWAYS
        style {
            prefWidth = Screen.getPrimary().visualBounds.width.px - 50.0
            prefHeight = Screen.getPrimary().visualBounds.height.px - 50.0
        }
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
                                    add(card {
                                        addClass(DefaultStyles.defaultCard)
                                        cardfront {
                                            innercard {
                                                title = it.labelKey.toUpperCase()
                                                bodyText = it.titleKey
                                                style {
                                                    maxHeight = 118.px
                                                    maxWidth = 142.px
                                                    backgroundImage += URI("/images/chapter_image.png")
                                                    backgroundColor += AppTheme.colors.lightBackground
                                                    borderColor += box(Color.WHITE)
                                                    borderWidth += box(3.0.px)
                                                    borderRadius += box(5.0.px)
                                                    borderInsets += box(1.5.px)
                                                }
                                            }
                                            cardbutton {
                                                addClass(DefaultStyles.defaultCardButton)
                                                text = messages["openProject"]
                                                graphic = MaterialIconView(MaterialIcon.ARROW_FORWARD, "25px")
                                                        .apply { fill = AppTheme.colors.appRed }
                                                action {
                                                    viewModel.selectChildCollection(it)
                                                    verseScrollPane.show()
                                                    chapterScrollPane.hide()
                                                }
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
                                    add(card {
                                        addClass(DefaultStyles.defaultCard)
                                        cardfront {
                                            innercard {
                                                title = it.first.value.labelKey.toUpperCase()
                                                bodyText = it.first.value.start.toString()
                                                style {
                                                    maxHeight = 118.px
                                                    maxWidth = 142.px
                                                    backgroundImage += URI("/images/verse_image.png")
                                                    backgroundColor += AppTheme.colors.lightBackground
                                                    borderColor += box(Color.WHITE)
                                                    borderWidth += box(3.0.px)
                                                    borderRadius += box(5.0.px)
                                                    borderInsets += box(1.5.px)
                                                }
                                            }
                                            cardbutton {
                                                addClass(DefaultStyles.defaultCardButton)
                                                text = messages["openProject"]
                                                graphic = MaterialIconView(MaterialIcon.ARROW_FORWARD, "25px")
                                                        .apply { fill = AppTheme.colors.appRed }
                                                action {
                                                    viewModel.viewContentTakes(it.first.value)
                                                    verseScrollPane.show()
                                                    chapterScrollPane.hide()
                                                }
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
            } else if (it == null && viewModel.activeChildProperty.value == null) {
                //user navigated back to chapter collection
                showAvailableChapters()
            }
        }

    }


     fun showAvailableChapters() {
        verseScrollPane.hide()
        chapterScrollPane.show()
        viewModel.activeChildProperty.value = null
    }

    override fun onUndock() {
        super.onUndock()
    }

}



