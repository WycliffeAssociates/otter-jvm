package org.wycliffeassociates.otter.jvm.app.ui.mainscreen

import com.sun.javafx.css.Stylesheet
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Orientation
import javafx.scene.effect.DropShadow
import javafx.scene.layout.*
import javafx.scene.paint.Color
import org.wycliffeassociates.otter.jvm.app.images.ImageLoader
import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
import org.wycliffeassociates.otter.jvm.app.ui.projecteditor.view.ProjectEditor
import org.wycliffeassociates.otter.jvm.app.ui.projecteditor.viewmodel.ProjectEditorViewModel
import org.wycliffeassociates.otter.jvm.app.ui.projecthome.view.ProjectHomeView
import org.wycliffeassociates.otter.jvm.app.widgets.projectnav.projectnav
import tornadofx.*
import java.net.URI

class MainScreenView: View() {
    override val root = hbox{}
    var activeFragment: Workspace = Workspace()
    var fragmentStage: AnchorPane by singleAssign()

    val viewModel :MainViewViewModel by inject()
    init {
//        importStylesheet("/custom.css")
        importStylesheet<MainScreenStyles>()
        activeFragment.header.removeFromParent()
        with(root) {
            style{
                backgroundColor += AppTheme.colors.defaultBackground
            }
            add(projectnav{
                style{
                    prefWidth = 200.px
                    minWidth = 200.px
                }
                navbox("Select a Book", MaterialIconView(MaterialIcon.BOOK, "25px")) {
                    var cardGraphic = ImageLoader.load(
                            ClassLoader.getSystemResourceAsStream("images/project_image.png"),
                            ImageLoader.Format.PNG
                    )
                    innercard(cardGraphic) {
                        style {
                            backgroundColor += AppTheme.colors.lightBackground
                            borderColor += box(Color.WHITE)
                            borderWidth += box(3.0.px)
                            borderRadius += box(5.0.px)
                            borderInsets += box(1.5.px)
                        }
                        majorLabelProperty.bind(viewModel.selectedProjectName)
                        minorLabelProperty.bind(viewModel.selectedProjectLanguage)
                        visibleProperty().bind(viewModel.selectedProjectProperty.booleanBinding{it != null})
                    }
                }
                navbox("Select a Chapter", MaterialIconView(MaterialIcon.CHROME_READER_MODE, "25px")) {
                    var cardGraphic = ImageLoader.load(
                            ClassLoader.getSystemResourceAsStream("images/chapter_image.png"),
                            ImageLoader.Format.PNG
                    )
                    innercard(cardGraphic) {
                        style {
                            backgroundColor += AppTheme.colors.lightBackground
                            borderColor += box(Color.WHITE)
                            borderWidth += box(3.0.px)
                            borderRadius += box(5.0.px)
                            borderInsets += box(1.5.px)
                        }
                        titleProperty.bind(viewModel.selectedCollectionTitle)
                        bodyTextProperty.bind(viewModel.selectedCollectionBody)
                        visibleProperty().bind(viewModel.selectedCollectionProperty.booleanBinding{it != null})
                    }
                }
                navbox("Select a Verse", MaterialIconView(MaterialIcon.BOOKMARK, "25px")) {
                    var cardGraphic = ImageLoader.load(
                            ClassLoader.getSystemResourceAsStream("images/verse_image.png"),
                            ImageLoader.Format.PNG
                    )
                    innercard(cardGraphic){
                        style {
                            backgroundColor += AppTheme.colors.lightBackground
                            borderColor += box(Color.WHITE)
                            borderWidth += box(3.0.px)
                            borderRadius += box(5.0.px)
                            borderInsets += box(1.5.px)
                        }
                        titleProperty.bind(viewModel.selectedContentTitle)
                        bodyTextProperty.bind(viewModel.selectedContentBody)
                        visibleProperty().bind(viewModel.selectedContentProperty.booleanBinding{it != null})
                    }
                }
                navButton {
                    text = "Back"
                    graphic = MaterialIconView(MaterialIcon.ARROW_BACK,"25px")
                    style{
                        backgroundColor += AppTheme.colors.white
                        textFill = AppTheme.colors.defaultText
                        borderColor += box(AppTheme.colors.lightBackground)
                        backgroundRadius += box(25.px)
                        borderRadius += box(25.px)
                        effect = DropShadow(2.0,2.0, 2.0, AppTheme.colors.defaultBackground)
                        prefWidth = 90.px
                    }
                    action {
                       navigateBack()
                    }
                }
            })
            fragmentStage = anchorpane{
                hgrow = Priority.ALWAYS
                vgrow = Priority.ALWAYS
                add(listmenu{
                    orientation = Orientation.HORIZONTAL
                    item("Home", MaterialIconView(MaterialIcon.HOME, "20px"))
                    item("Profile", MaterialIconView(MaterialIcon.PERSON, "20px"))
                    item("Settings", MaterialIconView(MaterialIcon.SETTINGS, "20px"))

                    anchorpaneConstraints {
                        topAnchor = 0
                        rightAnchor = 0
                    }
                })
                     hbox {
                        anchorpaneConstraints {
                            topAnchor = 50
                            leftAnchor = 50
                            rightAnchor = 0
                            bottomAnchor = 0
                        }
                        style{
                            backgroundColor += Color.VIOLET
                        }
                        hgrow = Priority.ALWAYS
                        vgrow = Priority.ALWAYS
                         activeFragment.dock<ProjectHomeView>()
                         ProjectHomeView().apply {
                             viewModel.selectedProjectProperty.bind(activeProject)
                         }

                         add(activeFragment)
                    }
            }
        }
    }

    private fun navigateBack() {

        if(viewModel.selectedContentProperty.value != null) {
            find(ProjectEditorViewModel::class).activeContentProperty.value = null
            activeFragment.navigateBack()
        }
        else if(viewModel.selectedContentProperty.value == null && viewModel.selectedCollectionProperty.value != null) {
            find(ProjectEditor::class).showAvailableChapters()
//            activeFragment.navigateBack()
        }

        else if(viewModel.selectedContentProperty.value == null && viewModel.selectedCollectionProperty.value == null) {
            find(ProjectEditorViewModel::class).activeChildProperty.value = null
            activeFragment.navigateBack()
        }

    }

}