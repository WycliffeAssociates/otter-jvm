package org.wycliffeassociates.otter.jvm.app.ui.removeplugins

import com.github.thomasnield.rxkotlinfx.observeOnFx
import javafx.collections.FXCollections
import org.wycliffeassociates.otter.common.data.audioplugin.AudioPluginData
import org.wycliffeassociates.otter.common.domain.plugins.AccessPlugins
import org.wycliffeassociates.otter.jvm.app.ui.inject.Injector
import tornadofx.ViewModel

class RemovePluginsViewModel : ViewModel() {
    val pluginRepository = Injector.pluginRepository

    val plugins = FXCollections.observableArrayList<AudioPluginData>()

    val accessPlugins = AccessPlugins(pluginRepository)

    fun refreshPlugins() {
        plugins.clear()
        accessPlugins
                .getAllPluginData()
                .observeOnFx()
                .subscribe { pluginData ->
                    plugins.addAll(pluginData)
                }
    }

    fun remove(plugin: AudioPluginData) {
        plugins.remove(plugin)
        accessPlugins.delete(plugin).subscribe()
    }
}