package org.wycliffeassociates.otter.jvm.app.widgets.chaptercard

import tornadofx.*

class ChapterCardStyles: Stylesheet() {
    companion object {
        val defaultCardBackground by cssclass()
    }

    init {
        defaultCardBackground {
            prefWidth = 180.px
            prefHeight = 70.px
            maxHeight = 70.px
            backgroundRadius += box(0.0.px, 0.0.px, 25.0.px, 25.0.px)
        }
    }
}