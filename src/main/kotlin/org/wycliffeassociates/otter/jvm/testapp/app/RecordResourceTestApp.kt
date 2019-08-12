package org.wycliffeassociates.otter.jvm.testapp.app

import tornadofx.*

class RecordResourceTestApp: App(RecordResourceTestView::class) {
}
fun main(args: Array<String>) {
    launch<RecordResourceTestApp>(args)
}
