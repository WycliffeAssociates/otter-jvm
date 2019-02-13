package org.wycliffeassociates.otter.jvm.recorder.app

import org.wycliffeassociates.otter.jvm.recorder.app.view.RecorderView
import tornadofx.App
import tornadofx.launch

fun main(args: Array<String>) {

    if(args.isNotEmpty()) {
        args[0] = "--wav=" + args[0]
    }

    launch<RecordingApp>(args)
}

class RecordingApp: App(RecorderView::class)