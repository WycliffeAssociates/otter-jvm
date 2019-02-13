package org.wycliffeassociates.otter.jvm.recorder.app.view

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Paint
import tornadofx.Fragment
import tornadofx.hbox
import tornadofx.label

class InfoFragment(
        language: String = "",
        book: String = "",
        chapter: String = "",
        chunk: String = ""
) : Fragment() {
    override val root = hbox {
        minHeight = 50.0
        alignment = Pos.CENTER_LEFT
        label(language)
        label(book)
        label(chapter)
        label(chunk)

        background = Background(BackgroundFill(Paint.valueOf("#333333"), CornerRadii.EMPTY, Insets.EMPTY))
    }
}