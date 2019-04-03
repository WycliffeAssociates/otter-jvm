//package org.wycliffeassociates.otter.jvm.resourcestestapp.viewmodel
//
//import javafx.collections.ObservableList
////import org.wycliffeassociates.otter.jvm.resourcestestapp.model.Resource
//import tornadofx.ViewModel
//import javafx.collections.FXCollections
//import org.wycliffeassociates.otter.jvm.resourcestestapp.view.ResourceTakesFragment
//import org.wycliffeassociates.otter.jvm.resourcestestapp.view.ResourcesView
//import tornadofx.find
//
//class ResourcesViewModel : ViewModel() {
//
////    val resourceList: ObservableList<Resource> = FXCollections.observableArrayList()
//
//    fun goToResourceTakesFragment() {
//        find<ResourcesView>().activeFragment.dock<ResourceTakesFragment>()
//    }
//
//    // TODO: Remove
//    companion object {
//        fun goToResourceTakesFragment() {
//            find<ResourcesView>().activeFragment.dock<ResourceTakesFragment>()
//        }
//    }
//}