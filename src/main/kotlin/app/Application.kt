package app

import app.ui.MainView
import tornadofx.*
import widgets.profileIcon.view.ProfileIconStyle

//object Application {
//    @JvmStatic
fun main(args: Array<String>) {
    launch<MainApp>()
}


class MainApp : App(MainView::class, ProfileIconStyle::class)
