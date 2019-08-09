package org.wycliffeassociates.otter.jvm.app.ui.takemanagement.view

import com.github.thomasnield.rxkotlinfx.toObservable
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.ContentDisplay
import javafx.scene.layout.Priority
import org.wycliffeassociates.otter.common.data.workbook.Take
import org.wycliffeassociates.otter.jvm.app.theme.AppStyles
import org.wycliffeassociates.otter.jvm.app.ui.takemanagement.viewmodel.RecordScriptureViewModel
import org.wycliffeassociates.otter.jvm.app.widgets.dragtarget.DragTargetBuilder
import org.wycliffeassociates.otter.jvm.app.widgets.takecard.*
import tornadofx.*

private class RecordableViewModelProvider: Component() {
    private val recordScriptureViewModel: RecordScriptureViewModel by inject()
    fun get() = recordScriptureViewModel.recordableViewModel
}

class RecordScriptureFragment : RecordableFragment(
    RecordableViewModelProvider().get(),
    DragTargetBuilder(DragTargetBuilder.Type.SCRIPTURE_TAKE)
) {
    private val recordScriptureViewModel: RecordScriptureViewModel by inject()

    init {
        importStylesheet<RecordScriptureStyles>()
        importStylesheet<TakeCardStyles>()

        mainContainer.apply {
            addClass(RecordScriptureStyles.background)
            hbox(15.0) {
                addClass(RecordScriptureStyles.pageTop)
                alignment = Pos.CENTER
                vgrow = Priority.ALWAYS
                button(messages["previousVerse"], AppStyles.backIcon()) {
                    addClass(RecordScriptureStyles.navigationButton)
                    action {
                        recordScriptureViewModel.previousChunk()
                    }
                    enableWhen(recordScriptureViewModel.hasPrevious)
                }
                vbox {
                    region {
                        vgrow = Priority.ALWAYS
                    }
                    add(dragTarget)
                    region {
                        vgrow = Priority.ALWAYS
                    }
                }

                button(messages["nextVerse"], AppStyles.forwardIcon()) {
                    addClass(RecordScriptureStyles.navigationButton)
                    contentDisplay = ContentDisplay.RIGHT
                    action {
                        recordScriptureViewModel.nextChunk()
                        enableWhen(recordScriptureViewModel.hasNext)
                    }
                }
            }

            scrollpane {
                vgrow = Priority.ALWAYS
                isFitToWidth = true
                addClass(RecordScriptureStyles.scrollpane)
                add(
                    TakesFlowPane(
                        primaryStage = primaryStage,
                        recordableViewModel = recordableViewModel,
//                        alternateTakes = recordableViewModel.alternateTakes,
                        createTakeCard = ::createTakeCard,
                        createRecordCard = ::createRecordCard,
                        createBlankCard = ::createBlankCard
                    ).apply {
//                        primaryStage.setOnShown {
//                            updateBlankCards()
//                        }
                    }
                )
            }
        }
    }

    override fun createTakeCard(take: Take): TakeCard {
        return scripturetakecard(
            take,
            audioPluginViewModel.audioPlayer(),
            lastPlayOrPauseEvent.toObservable()
        )
    }

    private fun createRecordCard(): Node {
        return vbox(10.0) {
            alignment = Pos.CENTER
            addClass(RecordScriptureStyles.newTakeCard, TakeCardStyles.scriptureTakeCardDropShadow)
            label(messages["newTake"])
            button(messages["record"], AppStyles.recordIcon("25px")) {
                action {
                    recordableViewModel.recordNewTake()
                }
            }
        }
    }

    private fun createBlankCard(): Node {
        return vbox(10.0) {
            alignment = Pos.CENTER
            addClass(TakeCardStyles.scriptureTakeCardPlaceholder, TakeCardStyles.scriptureTakeCardDropShadow)
        }
    }
}
