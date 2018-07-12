package app.ui

import javafx.stage.Stage
import tornadofx.*
import widgets.profile.icon.view.MyView
import widgets.profile.icon.view.Styles

//object Application {
//    @JvmStatic
fun main(args: Array<String>) {
    launch<MainView>()
}


class MainView : App(MyView::class, Styles::class) {
    override fun start(stage: Stage) {
        super.start(stage)
        stage.isFullScreen = false
    }
}
