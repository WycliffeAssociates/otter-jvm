package org.wycliffeassociates.otter.jvm.app.ui.chapterpage.viewModel

import io.reactivex.subjects.PublishSubject
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.wycliffeassociates.otter.jvm.app.ui.chapterpage.model.ChapterPageModel
import org.wycliffeassociates.otter.jvm.app.ui.chapterpage.model.Verse
import org.wycliffeassociates.otter.jvm.app.ui.chapterpage.view.Contexts
import tornadofx.*

class ChapterPageViewModel : ViewModel() {
    val model = ChapterPageModel()
    private var activeChapter:PublishSubject<Int> = PublishSubject.create<Int>()
    var selectedTab: PublishSubject<Contexts> = PublishSubject.create<Contexts>()
    val chapters: ObservableList<String> = FXCollections.observableArrayList<String>()
    val verses: ObservableList<Verse> = FXCollections.observableArrayList<Verse>()
    val bookTitle = model.bookTitle

    fun changeContext(context: Contexts) {
        selectedTab.onNext(context)
    }

    init {
        initalizeChapterPage()
    }

    private fun initalizeChapterPage() {
        var selectedChapter: Int
        model.chapters.forEach {
            //map list of chapters to an observable Array List, prepend "Chapter" string
            chapters.addAll(
                    messages["chapter"] + " " + it.chapterNumber.toString()
            )
        }

        //use observer to switch verses that are in verses observableList
        activeChapter.subscribe {
                selectedChapter = it
                //clear the arrayList to prevent verse duplications
                verses.clear()
                verses.addAll(
                        model.chapters
                                .filter { it.chapterNumber == selectedChapter }
                                .first()
                                .verses
                )
            }
    }

    fun selectedChapter(chapterIndex: Int) {
        activeChapter.onNext(chapterIndex)
    }
}