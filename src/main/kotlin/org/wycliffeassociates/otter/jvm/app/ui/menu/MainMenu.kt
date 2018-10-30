package org.wycliffeassociates.otter.jvm.app.ui.menu

import com.github.thomasnield.rxkotlinfx.observeOnFx
import de.jensd.fx.glyphs.materialicons.MaterialIcon
import de.jensd.fx.glyphs.materialicons.MaterialIconView
import io.reactivex.schedulers.Schedulers
import javafx.scene.control.MenuBar
import javafx.scene.control.ToggleGroup
import org.wycliffeassociates.otter.common.domain.ImportResourceContainer
import org.wycliffeassociates.otter.common.domain.plugins.AccessPlugins
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import tornadofx.*
import tornadofx.FX.Companion.messages

class MainMenu : MenuBar() {

    val languageRepo = Injector.languageRepo
    val metadataRepo = Injector.metadataRepo
    val collectionRepo = Injector.collectionRepo
    val chunkRepo = Injector.chunkRepo
    val directoryProvider = Injector.directoryProvider
    val pluginRepository = Injector.pluginRepository

    init {
        with(this) {
            menu(messages["file"]) {
                item(messages["importResource"]) {
                    graphic = MaterialIconView(MaterialIcon.INPUT, "20px")
                    action {
                        val file = chooseDirectory(messages["importResourceTip"])
                        file?.let {
                            val importer = ImportResourceContainer(
                                    languageRepo,
                                    metadataRepo,
                                    collectionRepo,
                                    chunkRepo,
                                    directoryProvider
                            )
                            importer.import(file)
                                    .subscribeOn(Schedulers.io()).observeOnFx()
                                    .subscribe { println("done")}
                        }
                    }
                }
                val accessPlugins = AccessPlugins(pluginRepository)
                val getPlugins = accessPlugins
                        .getAllPluginData()
                menu(messages["audioRecorder"]) {
                    graphic = MaterialIconView(MaterialIcon.MIC, "20px")
                    val pluginToggleGroup = ToggleGroup()
                    getPlugins
                            .observeOnFx()
                            .doOnSuccess { pluginData ->
                                pluginData.forEach {
                                    radiomenuitem(it.name) {
                                        userData = it
                                        action {
                                            accessPlugins.setRecorderData(it).subscribe()
                                        }
                                        toggleGroup = pluginToggleGroup
                                    }
                                }
                            }
                            .flatMapMaybe {
                                accessPlugins.getRecorderData()
                            }
                            .observeOnFx()
                            .subscribe { plugin ->
                                pluginToggleGroup
                                        .toggles
                                        .filter { it.userData == plugin }
                                        .firstOrNull()
                                        ?.isSelected = true
                            }
                }
                menu(messages["audioEditor"]) {
                    graphic = MaterialIconView(MaterialIcon.MODE_EDIT, "20px")
                    val pluginToggleGroup = ToggleGroup()
                    getPlugins
                            .observeOnFx()
                            .doOnSuccess { pluginData ->
                                pluginData.forEach {
                                    radiomenuitem(it.name) {
                                        userData = it
                                        action {
                                            accessPlugins.setEditorData(it).subscribe()
                                        }
                                        toggleGroup = pluginToggleGroup
                                    }
                                }
                            }
                            .flatMapMaybe {
                                accessPlugins.getEditorData()
                            }
                            .observeOnFx()
                            .subscribe { plugin ->
                                pluginToggleGroup
                                        .toggles
                                        .filter { it.userData == plugin }
                                        .firstOrNull()
                                        ?.isSelected = true
                            }
                }
            }

        }
    }
}