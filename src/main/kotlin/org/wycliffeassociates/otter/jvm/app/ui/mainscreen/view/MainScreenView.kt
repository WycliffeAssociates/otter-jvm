package org.wycliffeassociates.otter.jvm.app.ui.mainscreen.view

import javafx.scene.Node
import javafx.scene.layout.*
import org.wycliffeassociates.otter.jvm.app.theme.AppStyles
import org.wycliffeassociates.otter.jvm.app.theme.AppTheme
import org.wycliffeassociates.otter.jvm.app.ui.chromeablestage.view.ChromeableStage
import org.wycliffeassociates.otter.jvm.app.ui.mainscreen.NavBoxType
import org.wycliffeassociates.otter.jvm.app.ui.mainscreen.viewmodel.MainScreenViewModel
import org.wycliffeassociates.otter.jvm.app.ui.workbook.viewmodel.WorkbookViewModel
import org.wycliffeassociates.otter.jvm.app.widgets.projectnav.projectnav
import tornadofx.*

class MainScreenView : View() {
    override val root = hbox {}
    var activeFragment: Workspace = Workspace()
    private val navigator: ChromeableStage by inject()

    val viewModel: MainScreenViewModel by inject()
    val workbookViewModel: WorkbookViewModel by inject()
    private val chromeableStage: ChromeableStage by inject()

    data class NavBoxItem(val defaultText: String, val textGraphic: Node, val cardGraphic: Node, val type: NavBoxType)

    val navboxList: List<NavBoxItem> = listOf(
        NavBoxItem(messages["selectBook"], AppStyles.bookIcon("25px"), AppStyles.projectGraphic(), NavBoxType.PROJECT),
        NavBoxItem(
            messages["selectChapter"],
            AppStyles.chapterIcon("25px"),
            AppStyles.chapterGraphic(),
            NavBoxType.CHAPTER
        ),
        NavBoxItem(messages["selectVerse"], AppStyles.verseIcon("25px"), AppStyles.chunkGraphic(), NavBoxType.CHUNK)
    )

    init {
        importStylesheet<MainScreenStyles>()
        activeFragment.header.removeFromParent()
        with(root) {
            addClass(MainScreenStyles.main)
            style {
                backgroundColor += AppTheme.colors.defaultBackground
            }
            add(
                projectnav {
                    style {
                        prefWidth = 200.px
                        minWidth = 200.px
                    }
                    navboxList.forEach {
                        navbox(it.defaultText, it.textGraphic) {
                            innercard(it.cardGraphic) {
                                when (it.type) {
                                    NavBoxType.PROJECT -> {
                                        majorLabelProperty.bind(viewModel.selectedProjectName)
                                        minorLabelProperty.bind(viewModel.selectedProjectLanguage)
                                        visibleProperty()
                                            .bind(workbookViewModel.activeWorkbookProperty.booleanBinding { it != null })
                                    }
                                    NavBoxType.CHAPTER -> {
                                        titleProperty.bind(viewModel.selectedChapterTitle)
                                        bodyTextProperty.bind(viewModel.selectedChapterBody)
                                        visibleProperty()
                                            .bind(workbookViewModel.activeChapterProperty.booleanBinding { it != null })
                                    }
                                    NavBoxType.CHUNK -> {
                                        titleProperty.bind(viewModel.selectedChunkTitle)
                                        bodyTextProperty.bind(viewModel.selectedChunkBody)
                                        visibleProperty()
                                            .bind(workbookViewModel.activeChunkProperty.booleanBinding { it != null })
                                    }
                                }
                            }
                        }
                    }
                    navButton {
                        text = messages["back"]
                        graphic = AppStyles.backIcon()
                        action {
                            navigator.back()
                        }
                    }
                }
            )

            chromeableStage.root.apply {
                hgrow = Priority.ALWAYS
                vgrow = Priority.ALWAYS
            }
            add(chromeableStage.root)
        }
    }
}