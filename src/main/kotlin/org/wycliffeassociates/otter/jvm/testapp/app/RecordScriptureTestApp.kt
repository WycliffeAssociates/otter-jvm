package org.wycliffeassociates.otter.jvm.testapp.app

import tornadofx.*

class RecordScriptureTestApp: App(RecordScriptureTestView::class) {
}
fun main(args: Array<String>) {
    launch<RecordScriptureTestApp>(args)
}
