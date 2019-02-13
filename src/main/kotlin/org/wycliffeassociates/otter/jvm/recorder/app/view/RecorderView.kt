package org.wycliffeassociates.otter.jvm.recorder.app.view

import javafx.stage.Screen
import tornadofx.View
import tornadofx.vbox


class RecorderView : View() {
    override val root = vbox {
        prefHeight = Screen.getPrimary().visualBounds.height - 50.0
        prefWidth = Screen.getPrimary().visualBounds.width - 50.0

        add(InfoFragment())
        add(RecordingVisualizerFragment())
        add(ControlFragment())
    }
}