package org.wycliffeassociates.otter.jvm.app.ui.mainscreen

import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import javafx.geometry.Orientation
import javafx.scene.layout.*
import org.wycliffeassociates.otter.jvm.app.images.ImageLoader
import org.wycliffeassociates.otter.jvm.app.theme.AppStyles
import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
import org.wycliffeassociates.otter.jvm.app.ui.projecthome.view.ProjectHomeView
import org.wycliffeassociates.otter.jvm.app.widgets.projectnav.projectnav
import tornadofx.*

class MainScreenView: View() {
    override val root = hbox{}
    var activeFragment: Workspace = Workspace()
    var fragmentStage: AnchorPane by singleAssign()

    val viewModel :MainViewViewModel by inject()
    init {
        importStylesheet<MainScreenStyles>()
        activeFragment.header.removeFromParent()
        with(root) {
            addClass(MainScreenStyles.main)
            style{
                backgroundColor += AppTheme.colors.defaultBackground
            }
            add(projectnav{
                style{
                    prefWidth = 200.px
                    minWidth = 200.px
                }
                navbox(messages["selectBook"], MaterialIconView(MaterialIcon.BOOK, "25px")) {
                    var cardGraphic = ImageLoader.load(
                            ClassLoader.getSystemResourceAsStream("images/project_image.png"),
                            ImageLoader.Format.PNG
                    )
                    innercard(cardGraphic) {
                        addClass(MainScreenStyles.navBoxInnercard)
                        majorLabelProperty.bind(viewModel.selectedProjectName)
                        minorLabelProperty.bind(viewModel.selectedProjectLanguage)
                        visibleProperty().bind(viewModel.selectedProjectProperty.booleanBinding{it != null})
                    }
                }
                navbox(messages["selectChapter"], MaterialIconView(MaterialIcon.CHROME_READER_MODE, "25px")) {
                    var cardGraphic = ImageLoader.load(
                            ClassLoader.getSystemResourceAsStream("images/chapter_image.png"),
                            ImageLoader.Format.PNG
                    )
                    innercard(cardGraphic) {
                       addClass(MainScreenStyles.navBoxInnercard)
                        titleProperty.bind(viewModel.selectedCollectionTitle)
                        bodyTextProperty.bind(viewModel.selectedCollectionBody)
                        visibleProperty().bind(viewModel.selectedCollectionProperty.booleanBinding{it != null})
                    }
                }
                navbox(messages["selectVerse"], MaterialIconView(MaterialIcon.BOOKMARK, "25px")) {
                    var cardGraphic = ImageLoader.load(
                            ClassLoader.getSystemResourceAsStream("images/verse_image.png"),
                            ImageLoader.Format.PNG
                    )
                    innercard(cardGraphic){
                        addClass(MainScreenStyles.navBoxInnercard)
                        titleProperty.bind(viewModel.selectedContentTitle)
                        bodyTextProperty.bind(viewModel.selectedContentBody)
                        visibleProperty().bind(viewModel.selectedContentProperty.booleanBinding{it != null})
                    }
                }
                navButton {
                    text = messages["back"]
                    graphic = AppStyles.backIcon()
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
                    item(messages["home"], MaterialIconView(MaterialIcon.HOME, "20px"))
                    item(messages["profile"], MaterialIconView(MaterialIcon.PERSON, "20px"))
                    item(messages["settings"], MaterialIconView(MaterialIcon.SETTINGS, "20px"))

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
                        hgrow = Priority.ALWAYS
                        vgrow = Priority.ALWAYS
                         activeFragment.dock<ProjectHomeView>()
                         ProjectHomeView().apply {
                             viewModel.selectedProjectProperty.bindBidirectional(activeProject)
                         }
                         add(activeFragment)
                    }
            }
        }
    }

    private fun navigateBack() {

        //navigate back to verse selection from viewing takes
        if(viewModel.selectedContentProperty.value != null) {
            viewModel.selectedContentProperty.value = null
            activeFragment.navigateBack()
        }
        //from verse selection, navigate back to chapter selection
        else if(viewModel.selectedContentProperty.value == null && viewModel.selectedCollectionProperty.value != null) {
            viewModel.selectedCollectionProperty.value = null
            activeFragment.navigateBack()
        }

        //navigate back to the projecthome view
        else if(viewModel.selectedContentProperty.value == null && viewModel.selectedCollectionProperty.value == null) {
            activeFragment.navigateBack()
        }

    }

}